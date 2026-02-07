package com.server.mechanics;

import com.server.config.AppConfig;
import com.server.util.json.VxSerializable;
import com.server.mechanics.database.GameDatabase;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;

@Slf4j
public final class Main {

    public static void main(String[] args) {

        // Create the Guice injector and bind our game dependencies
        Injector injector = Guice.createInjector(new GameModule());
        AppConfig config = injector.getInstance(AppConfig.class);
        GameDatabase database = injector.getInstance(GameDatabase.class);

        VertxOptions options = new VertxOptions();
        options.setBlockedThreadCheckInterval(config.vertxBlockedThreadCheckIntervalMs());
        options.setMaxEventLoopExecuteTime(config.vertxMaxEventLoopExecuteTimeMs());
        Vertx vertx = Vertx.vertx(options);

        // Scan the package for all classes implementing VxSerializable and register them with Vert.x event bus codecs.
        // This is only run at startup, so performance impact is negligible.
        Reflections reflections = new Reflections(Main.class.getPackageName());
        Set<Class<?>> classes = reflections.get(SubTypes.of(VxSerializable.class).asClass());
        VxSerializable.registerClasses(vertx, classes);
        VxSerializable.registerDefaults();

        if (!database.migrate(config.flywayLocations()))
            System.exit(-1);

        // Deploy the HTTP verticle across all available CPU cores
        vertx.deployVerticle(() ->
                                new HttpVerticle(config.httpPort()),
                                new DeploymentOptions().setInstances(config.httpInstances()))
            .onFailure(ex -> {
                log.error("Failed to start server", ex);
                System.exit(-1);
            });

        // Clean up resources on JVM exit
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutting down server...");
            database.shutdown();
            vertx.close().onComplete(ar -> {
                if (ar.succeeded())
                    log.info("Vert.x shut down cleanly");
                else
                    log.error("Error shutting down Vert.x", ar.cause());
            });
        }));

    }

}
