package com.server.websocket.exception;

import com.server.util.exceptions.VertxJsonException;

public final class OperationNotImplementedException extends VertxJsonException {

    public OperationNotImplementedException(String operation) {
        super("NotImplemented", 501, String.format("Operation %s is not implemented", operation));
    }

}
