package com.server.util.json;

import com.server.websocket.handler.WebSocketRoutingContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

import java.util.Optional;

public class VertxJsonResponse {

    public static void endWithVoidResult(RoutingContext ctx, int statusCodeIfOk, Optional<Void> result) {
        if (result.isPresent())
            ctx.response().setStatusCode(statusCodeIfOk).end();
    }

    public static void endWithResult(RoutingContext ctx, int statusCodeIfOk, Optional<?> result) {
        result.ifPresent(o -> end(ctx, statusCodeIfOk, o));
    }

    public static void end(RoutingContext ctx, int statusCode, Object obj) {
        ctx.response()
                .putHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                .setStatusCode(statusCode)
                .end(Json.encode(obj));
    }

    public static void end(RoutingContext ctx, int statusCode, JsonObject jsObj) {
        ctx.response()
                .putHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                .setStatusCode(statusCode)
                .end(jsObj.encode());
    }

    public static void endWithVoidResult(WebSocketRoutingContext ctx, int statusCodeIfOk, Optional<Void> result) {
        if (result.isPresent())
            ctx.response().setStatusCode(statusCodeIfOk).end();
    }

    public static void endWithResult(WebSocketRoutingContext ctx, int statusCodeIfOk, Optional<?> result) {
        result.ifPresent(o -> end(ctx, statusCodeIfOk, o));
    }

    public static void end(WebSocketRoutingContext ctx, int statusCode, Object obj) {
        ctx.response().setStatusCode(statusCode).end(obj);
    }

    public static void end(WebSocketRoutingContext ctx, int statusCode, JsonObject jsObj) {
        ctx.response().setStatusCode(statusCode).end(jsObj);
    }

    public static void end(WebSocketRoutingContext ctx, int statusCode, String serializedMessage) {
        ctx.response().setStatusCode(statusCode).end(serializedMessage);
    }

}
