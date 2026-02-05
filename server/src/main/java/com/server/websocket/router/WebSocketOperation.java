package com.server.websocket.router;

import com.server.handlers.VertxExceptionHandler;
import com.server.websocket.exception.OperationNotImplementedException;
import com.server.websocket.handler.WebSocketRoutingContext;
import io.vertx.core.Handler;

public final class WebSocketOperation {
    private Handler<WebSocketRoutingContext> contextHandler;

    public WebSocketOperation(String operation) {
        contextHandler = ctx
                -> VertxExceptionHandler.handle(new OperationNotImplementedException(operation), ctx);
    }

    public WebSocketOperation handler(Handler<WebSocketRoutingContext> handler) {
        contextHandler = handler;
        return this;
    }

    public void handle(WebSocketRoutingContext ctx) {
        contextHandler.handle(ctx);
    }

}
