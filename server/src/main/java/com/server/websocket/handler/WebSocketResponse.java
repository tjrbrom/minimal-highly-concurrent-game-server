package com.server.websocket.handler;

import com.server.util.exceptions.VertxJsonException;
import com.server.websocket.WebSocketResponseType;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class WebSocketResponse {
    private final ServerWebSocket ws;
    private final WebSocketMessageHeader header;
    private short statusCode = 200;

    WebSocketResponse(ServerWebSocket ws, WebSocketMessageHeader header) {
        this.ws = ws;
        this.header = header;
    }

    public WebSocketResponse setStatusCode(int statusCode) {
        this.statusCode = (short) statusCode;
        return this;
    }

    public WebSocketResponse setStatusCode(short statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public void end(String nonSerializedMessage) {
        write(nonSerializedMessage);
    }

    public void end(Object object) {
        write(object);
    }

    public void end() {
        write(null);
    }

    public void error(String message) {
        if (ws.isClosed()) {
            log.error("WebSocket (Closed) failed response - requestId={} error=Internal message={}", header.requestUid(), message);
            return;
        }

        ws.writeTextMessage(new JsonObject()
                .put("ws-header", header.toJson())
                .put("ws-type", WebSocketResponseType.Response.name())
                .put("ws-utc", System.currentTimeMillis())
                .put("ws-code", statusCode)
                .put("ws-body", message)
                .encode());


        log.error("WebSocket message failed: requestId={} error=Internal message={}", header.requestUid(), message);
    }

    public void error(VertxJsonException exception) {
        if (ws.isClosed()) {
            log.error("WebSocket (Closed) failed response - requestId={} error={}", header.requestUid(), exception.error);
            return;
        }

        ws.writeTextMessage(new JsonObject()
                .put("ws-header", header.toJson())
                .put("ws-type", WebSocketResponseType.Response.name())
                .put("ws-utc", System.currentTimeMillis())
                .put("ws-code", statusCode)
                .put("ws-body", exception.toJson())
                .encode());

        log.error("WebSocket message failed: requestId={} error={}", header.requestUid(), exception.error);
    }

    private void write(Object object) {
        if (ws.isClosed()) {
            log.info("WebSocket (Closed) successful response - requestId={}", header.requestUid());
            return;
        }

        Object body;
        if (object == null) {
            body = "{}";
        } else {
            if (object instanceof String serialized) {
                body = Json.decodeValue(serialized);
            } else {
                body = object;
            }
        }

        log.info("WebSocket message successful: requestId={}", header.requestUid());
        ws.writeTextMessage(new JsonObject()
                .put("ws-header", header)
                .put("ws-type", WebSocketResponseType.Response.name())
                .put("ws-utc", System.currentTimeMillis())
                .put("ws-code", statusCode)
                .put("ws-body", body)
                .encode());
    }

}
