package com.server.launcher;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.server.cli.CliServerVerticle;
import com.server.handlers.JwtHandler;
import com.server.handlers.LoggerHandler;
import com.server.http.HttpVerticle;
import com.server.http.IRouter;
import com.server.util.json.VxSerializable;
import com.server.websocket.handler.WebSocketHandler;
import com.server.websocket.router.WebSocketRouter;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.sstore.SessionStore;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;

import java.util.List;
import java.util.Set;

import static org.reflections.scanners.Scanners.SubTypes;

@Slf4j
public final class GameLauncher {

    private final int cores;
    private final Vertx vertx;
    private final List<String> packages;
    private final Application app;
    private final List<IRouter> routers;
    private final List<WebSocketRouter> webSocketRouters;
    private String configPath = "config/settings.json";
    private JsonObject configuration;

    public GameLauncher(String configPath,
                        int cores,
                        Vertx vertx,
                        List<String> packages,
                        Application app,
                        List<IRouter> routers,
                        List<WebSocketRouter> webSocketRouters) {
        this.configPath = configPath;
        this.cores = cores;
        this.vertx = vertx;
        this.packages = packages;
        this.app = app;
        this.routers = routers;
        this.webSocketRouters = webSocketRouters;
    }

    public void start() {

        VxSerializable.registerDefaults();
        for (String pack : packages) {
            Reflections reflections = new Reflections(pack);
            Set<Class<?>> classes = reflections.get(SubTypes.of(VxSerializable.class).asClass());
            VxSerializable.registerClasses(vertx, classes);
        }

        Promise<JsonObject> cliConfig = Promise.promise();

        vertx.fileSystem().readFile("cli.config")
                .onSuccess(buffer -> cliConfig.complete(buffer.toJsonObject()))
                .onFailure(ex -> cliConfig.complete(new JsonObject()));

        cliConfig.future().compose(json -> {
            final int port = json.getJsonObject("server", new JsonObject())
                    .getInteger("port", 33333);
            final JsonObject credentials = json.getJsonObject("credentials");
            final String username = credentials != null ? credentials.getString("username") : null;
            final String password = credentials != null ? credentials.getString("password") : null;

            return vertx.deployVerticle(() -> new CliServerVerticle(vertx, app, port, username, password), new DeploymentOptions()
                            .setThreadingModel(ThreadingModel.EVENT_LOOP)
                            .setInstances(1))
                .compose(Void -> configuration())
                .compose(config -> {
                    configuration = config;
                    return jwtAuth();
                }).compose(jwtAuth -> {

                    SessionStore sessionStore = SessionStore.create(vertx);

                    LauncherModule launcherModule = new LauncherModule(vertx, configuration, new JwtHandler(jwtAuth), sessionStore);
                    Injector injector = Guice.createInjector(launcherModule);
                    routers.forEach(injector::injectMembers);

                    return vertx.deployVerticle(app, new DeploymentOptions()
                                .setInstances(1)
                                .setThreadingModel(ThreadingModel.EVENT_LOOP))
                        .compose(deploymentId -> {

                            Router router = createRouter(sessionStore);
                            WebSocketRouter wsRouter = createWebSocketRouter();

                            WebSocketHandler webSocketHandler = new WebSocketHandler(vertx, wsRouter, sessionStore);

                            return vertx.deployVerticle(
                                () -> new HttpVerticle(3333, router, webSocketHandler),
                                new DeploymentOptions()
                                    .setInstances(cores)
                                    .setThreadingModel(ThreadingModel.EVENT_LOOP)
                            );
                        })
                        .onSuccess(Void -> log.info("Successfully launched application"))
                        .onFailure(ex -> {
                            log.error("Failed to launch application", ex);
                            System.exit(-1);
                        });
                });
        });

    }

    private Future<JsonObject> configuration() {
        String filePath = (String) System.getProperties().getOrDefault("portal.config", configPath);
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setFormat("json")
                .setConfig(new JsonObject().put("path", filePath));

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

        Promise<JsonObject> promise = Promise.promise();
        retriever.getConfig()
                .onComplete(ar -> {
                    retriever.close();
                    if (ar.succeeded()) {
                        promise.complete(ar.result());
                    } else {
                        promise.fail(ar.cause());
                    }
                });

        return promise.future();
    }

    private Future<JWTAuth> jwtAuth() {
        if (!configuration.containsKey("keys")) {
            return Future.failedFuture(new Exception("Missing rsa keys"));
        }

        JsonObject keyConfig = configuration.getJsonObject("keys");
        if (!keyConfig.containsKey("public")) {
            return Future.failedFuture(new Exception("Missing public key in config"));
        } else if (!keyConfig.containsKey("private")) {
            return Future.failedFuture(new Exception("Missing private key in config"));
        }

        Future<Buffer> privateKey = vertx.fileSystem().readFile(keyConfig.getString("private"));
        Future<Buffer> publicKey = vertx.fileSystem().readFile(keyConfig.getString("public"));

        return Future.all(privateKey, publicKey)
                .compose(ar -> {
                    JWTAuthOptions options = new JWTAuthOptions()
                            .addPubSecKey(new PubSecKeyOptions()
                                    .setAlgorithm("RS256")
                                    .setBuffer(publicKey.result()))
                            .addPubSecKey(new PubSecKeyOptions()
                                    .setAlgorithm("RS256")
                                    .setBuffer(privateKey.result()));

                    return Future.succeededFuture(JWTAuth.create(vertx, options));
                });
    }

    private Router createRouter(SessionStore sessionStore) {

        Router root = Router.router(vertx);

        root.route().handler(SessionHandler.create(sessionStore));
        root.route().handler(new LoggerHandler());
        root.route().handler(BodyHandler.create());

        for (IRouter router : routers) {
            Router r = router.createRouter();
            if (r == null)
                continue;
            root.route().subRouter(r);
        }

        return root;
    }

    private WebSocketRouter createWebSocketRouter() {
        WebSocketRouter root = new WebSocketRouter();
        for (WebSocketRouter webSocketRouter : webSocketRouters) {
            root.subRouter(webSocketRouter);
        }
        for (IRouter router : routers) {
            WebSocketRouter wsRouter = router.createWebSocketRouter();
            if (wsRouter == null) continue;
            root.subRouter(wsRouter);
        }

        return root;
    }

}
