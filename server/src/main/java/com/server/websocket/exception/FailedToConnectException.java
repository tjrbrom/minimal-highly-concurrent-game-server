package com.server.websocket.exception;

import com.server.util.exceptions.VertxJsonException;

public final class FailedToConnectException extends VertxJsonException {

    public FailedToConnectException() {
        super("SessionFailedToConnect", 500, "Failed to open websocket connection");
    }

}
