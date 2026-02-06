package com.game.app;

import com.google.inject.Inject;
import com.server.http.IRouter;
import com.server.websocket.router.WebSocketRouter;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;

public final class GameRouter implements IRouter {

    @Inject
    private Vertx vertx;

    @Override
    public Router createRouter() {

        Router router = Router.router(vertx);

        // TODO: add game specific routers

        return router;
    }

    @Override
    public WebSocketRouter createWebSocketRouter() {

        WebSocketRouter router = new WebSocketRouter();

        // TODO: add game specific routers

        return router;
    }

}
