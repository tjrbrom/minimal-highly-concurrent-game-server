package com.server.websocket.router;

import com.server.handlers.VertxExceptionHandler;
import com.server.websocket.exception.OperationNotFoundException;
import com.server.websocket.handler.WebSocketRoutingContext;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class WebSocketRouter {
    private final Map<String, WebSocketOperation> operations;

    public WebSocketRouter() {
        operations = new HashMap<>();
    }

    public WebSocketOperation addOperation(String operation) {
        if (!operations.containsKey(operation)) {
            WebSocketOperation webSocketOperation = new WebSocketOperation(operation);
            operations.put(operation, webSocketOperation);
            return webSocketOperation;
        } else {
            return operations.get(operation);
        }
    }

    public void handle(WebSocketRoutingContext ctx) {
        if (operations.containsKey(ctx.header().action())) {
            operations.get(ctx.header().action()).handle(ctx);
        } else {
            OperationNotFoundException ex = new OperationNotFoundException(ctx.header().action());
            VertxExceptionHandler.handle(ex, ctx);
        }
    }

    public void subRouter(WebSocketRouter webSocketRouter) {
        for (String operation : webSocketRouter.operations.keySet()) {
            if (operations.containsKey(operation)) {
                log.warn("Operation {} is already registered", operation);
                continue;
            }
            operations.put(operation, webSocketRouter.operations.get(operation));
        }
    }

}
