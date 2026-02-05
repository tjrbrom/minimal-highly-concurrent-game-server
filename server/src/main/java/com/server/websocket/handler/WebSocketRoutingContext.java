package com.server.websocket.handler;

import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.auth.User;
import io.vertx.ext.web.RequestBody;
import io.vertx.ext.web.Session;
import lombok.Getter;

import java.util.Set;

public final class WebSocketRoutingContext {
    @Getter
    private final long packetTime;
    private final ServerWebSocket ws;
    private final WebSocketRequestBody body;
    private final WebSocketMessageHeader header;
    private final Session session;
    private final User user;
    private final Set<String> registeredEvents;

    public WebSocketRoutingContext(long packetTime,
                                   ServerWebSocket ws,
                                   WebSocketMessageHeader header,
                                   WebSocketRequestBody body,
                                   Session session,
                                   User user,
                                   Set<String> registeredEvents) {
        this.registeredEvents = registeredEvents;
        this.packetTime = packetTime;
        this.ws = ws;
        this.body = body;
        this.header = header;
        this.session = session;
        this.user = user;
    }

    public void bindEvent(String event) {
        registeredEvents.add(event);
    }

    public void unbindEvent(String event) {
        registeredEvents.remove(event);
    }

    public WebSocketMessageHeader header() {
        return header;
    }

    public RequestBody body() {
        return body;
    }

    public WebSocketResponse response() {
        return new WebSocketResponse(ws, header);
    }

    public Session session() {
        return session;
    }

    public User user() {
        return user;
    }

}
