package com.server.http;

import com.server.websocket.router.WebSocketRouter;
import io.vertx.ext.web.Router;

public interface IRouter {

    Router createRouter();

    WebSocketRouter createWebSocketRouter();

}
