package com.server.websocket.handler;

import com.server.websocket.router.WebSocketRouter;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.sstore.SessionStore;
import lombok.extern.slf4j.Slf4j;

/**
 * TODO: implement
 */
@Slf4j
public final class WebSocketHandler implements Handler<ServerWebSocket> {

    private final Vertx vertx;
    private final WebSocketRouter router;
    private final SessionStore sessionStore;

    public WebSocketHandler(Vertx vertx, WebSocketRouter router, SessionStore sessionStore) {
        this.vertx = vertx;
        this.router = router;
        this.sessionStore = sessionStore;
    }

    @Override
    public void handle(ServerWebSocket webSocket) {

        // TODO: implement

    }

}

