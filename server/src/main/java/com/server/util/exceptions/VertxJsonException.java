package com.server.util.exceptions;

import com.server.util.json.VxSerializable;
import io.vertx.core.json.JsonObject;

public abstract class VertxJsonException extends Exception implements VxSerializable {

    public final String error;
    public final int statusCode;
    public final String message;
    protected JsonObject data;

    protected VertxJsonException() {
        error = "500";
        statusCode = 500;
        message = "Something unexpected happened. Try again later";
        data = new JsonObject();
    }

    protected VertxJsonException(String error, int statusCode, String message) {
        this.error = error;
        this.statusCode = statusCode;
        this.message = message;
        data = new JsonObject();
    }

    public static JsonObject json() {
        return new JsonObject()
                .put("error", "Internal")
                .put("statusCode", 502)
                .put("message", "Something unexpected happened. Try again later")
                .put("data", new JsonObject());
    }

    public static String string() {
        return """
                {"error":"500","statusCode":500,"message":"Something unexpected happened. Try again later","data":{}}
                """;
    }

    public JsonObject toJson() {
        return new JsonObject()
                .put("error", error)
                .put("statusCode", statusCode)
                .put("message", message)
                .put("data", data);
    }
}
