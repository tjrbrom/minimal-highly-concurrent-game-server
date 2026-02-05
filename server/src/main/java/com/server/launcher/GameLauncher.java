package com.server.launcher;

import com.server.cli.CliServerVerticle;
import com.server.util.json.VxSerializable;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;

@Slf4j
public final class GameLauncher {

    private final Vertx vertx;
    private final List<String> packages;
    private final GameApp app;

    private GameLauncher(Vertx vertx,
                         List<String> packages,
                         GameApp app) {
        this.vertx = vertx;
        this.packages = packages;
        this.app = app;
    }

    public void start() {

        VxSerializable.registerDefaults();
        for (String pack : packages) {
            Reflections reflections = new Reflections(pack);
            Set<Class<?>> classes = reflections.get(SubTypes.of(VxSerializable.class).asClass());
            VxSerializable.registerClasses(vertx, classes);
        }

        Promise<JsonObject> config = Promise.promise();

        vertx.fileSystem().readFile("config")
                .onSuccess(buffer -> config.complete(buffer.toJsonObject()))
                .onFailure(ex -> config.complete(new JsonObject()));

        config.future().compose( json -> {
            final int port = json.getJsonObject("server", new JsonObject())
                                 .getInteger("port", 33333);
            final JsonObject credentials = json.getJsonObject("credentials");
            final String username = credentials != null ? credentials.getString("username") : null;
            final String password = credentials != null ? credentials.getString("password") : null;

            return vertx.deployVerticle(() -> new CliServerVerticle(vertx, app, port, username, password), new DeploymentOptions()
                            .setThreadingModel(ThreadingModel.VIRTUAL_THREAD)
                            .setInstances(1))
                    .compose(x -> Future.succeededFuture());
        });
    }

}
