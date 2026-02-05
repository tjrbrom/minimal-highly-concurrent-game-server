package com.server.websocket.handler;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RequestBody;

public class WebSocketRequestBody implements RequestBody {
    private final Buffer body;
    private String string;
    private JsonObject jsonObject;
    private JsonArray jsonArray;

    public WebSocketRequestBody(Buffer body) {
        this.body = body;
    }

    public String asString() {
        if (this.body == null) return null;

        if (this.string == null) {
            this.string = body.toString();
        }

        return this.string;
    }

    public String asString(String encoding) {
        return this.body == null ? null : this.body.toString(encoding);
    }

    @Override
    public JsonObject asJsonObject(int maxAllowedLength) {
        if (this.body == null) return null;

        if (this.jsonObject == null) {
            if (maxAllowedLength >= 0 && this.body.length() > maxAllowedLength) {
                throw new IllegalStateException("WebSocketRoutingContext body size exceeds the allowed limit");
            }

            this.jsonObject = (JsonObject) Json.decodeValue(this.body);
        }

        return this.jsonObject;
    }

    @Override
    public JsonArray asJsonArray(int maxAllowedLength) {
        if (this.body == null) return null;

        if (this.jsonArray == null) {
            if (maxAllowedLength >= 0 && this.body.length() > maxAllowedLength) {
                throw new IllegalStateException("WebSocketRoutingContext body size exceeds the allowed limit");
            }

            this.jsonArray = (JsonArray) Json.decodeValue(this.body);
        }

        return this.jsonArray;
    }

    public JsonObject asJsonObject() {
        if (this.body == null) return null;

        if (this.jsonObject == null) {
            this.jsonObject = (JsonObject) Json.decodeValue(this.body);
        }

        return this.jsonObject;
    }

    public JsonArray asJsonArray() {
        if (this.body == null) return null;

        if (this.jsonArray == null) {
            this.jsonArray = (JsonArray) Json.decodeValue(this.body);
        }

        return this.jsonArray;
    }

    public <T> T asPojo(Class<T> clazz) {
        return this.body == null ? null : Json.decodeValue(this.body, clazz);
    }

    public boolean isEmpty() {
        return this.length() <= 0;
    }

    @Override
    public boolean available() {
        return true;
    }

    public Buffer buffer() {
        return this.body;
    }

    @Override
    public <R> R asPojo(Class<R> clazz, int i) {
        return null;
    }

    public int length() {
        return this.body == null ? -1 : this.body.length();
    }

}
