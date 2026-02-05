package com.server.websocket.exception;

import com.server.util.exceptions.VertxJsonException;

public final class OperationNotFoundException extends VertxJsonException {

    public OperationNotFoundException(String operation) {
        super("OperationNotFound", 404, String.format("Operation %s not found", operation));
    }

}
