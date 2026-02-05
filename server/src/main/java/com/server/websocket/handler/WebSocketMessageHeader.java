package com.server.websocket.handler;

import com.server.util.json.VxSerializable;
import io.vertx.core.json.JsonObject;

public record WebSocketMessageHeader(String action,
                                     String requestUid) implements VxSerializable {

    public JsonObject toJson() {
        return JsonObject.of("action", action, "requestUid", requestUid);
    }

}
