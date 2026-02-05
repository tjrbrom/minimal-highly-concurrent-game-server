package com.server.launcher;

import com.server.cli.CliServerVerticle;
import com.server.handlers.LoggerHandler;
import com.server.http.HttpVerticle;
import com.server.http.IRouter;
import com.server.util.json.VxSerializable;
import com.server.websocket.handler.WebSocketHandler;
import com.server.websocket.router.WebSocketRouter;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
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
    private final GameApp app;
    private final List<IRouter> routers;
    private final List<WebSocketRouter> webSocketRouters;
    private String configPath = "config/settings.json";

    private GameLauncher(String configPath,
                         int cores,
                         Vertx vertx,
                         List<String> packages,
                         GameApp app,
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

        Promise<JsonObject> config = Promise.promise();

        vertx.fileSystem().readFile("cli.config")
                .onSuccess(buffer -> config.complete(buffer.toJsonObject()))
                .onFailure(ex -> config.complete(new JsonObject()));

        config.future().compose(json -> {
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

        SessionStore sessionStore = SessionStore.create(vertx);

        vertx.deployVerticle(app, new DeploymentOptions()
                        .setInstances(1)
                        .setThreadingModel(ThreadingModel.VIRTUAL_THREAD))
                .compose(deploymentId -> {

                    Router router = createRouter(sessionStore);
                    WebSocketRouter wsRouter = createWebSocketRouter();

                    WebSocketHandler webSocketHandler = new WebSocketHandler(vertx, wsRouter, sessionStore);

                    return vertx.deployVerticle(() -> new HttpVerticle(3333, router, true, webSocketHandler),
                                    new DeploymentOptions()
                                            .setThreadingModel(ThreadingModel.VIRTUAL_THREAD)
                                            .setInstances(1))
                            .compose(Void -> {
                                if (cores - 1 > 0) {
                                    return vertx.deployVerticle(() -> new HttpVerticle(3333, router, false, webSocketHandler),
                                                    new DeploymentOptions()
                                                            .setThreadingModel(ThreadingModel.VIRTUAL_THREAD)
                                                            .setInstances(cores - 1))
                                            .compose(res -> Future.succeededFuture());
                                }

                                return Future.succeededFuture();
                            });
                })
                .onSuccess(Void -> log.info("Successfully launched application"))
                .onFailure(ex -> {
                    log.error("Failed to launch application", ex);
                    System.exit(-1);
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
