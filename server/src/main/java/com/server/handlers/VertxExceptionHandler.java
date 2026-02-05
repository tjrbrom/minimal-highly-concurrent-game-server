package com.server.handlers;

import com.server.util.exceptions.VertxJsonException;
import com.server.websocket.handler.WebSocketRoutingContext;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VertxExceptionHandler {

    public static void handle(Throwable e, RoutingContext ctx) {
        var request = ctx.request();

        if (e instanceof VertxJsonException error) {
            log.error("{} {} - {} - error {}",
                    request.method().toString(),
                    request.absoluteURI(),
                    request.remoteAddress().hostAddress(),
                    error.message
            );

            ctx.response()
                    .putHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                    .setStatusCode(error.statusCode)
                    .end(error.toJson().encode());
        } else {
            log.error("{} {} - {} - error ",
                    request.method().toString(),
                    request.absoluteURI(),
                    request.remoteAddress().hostAddress(),
                    e
            );

            ctx.response()
                    .putHeader(HttpHeaderNames.CONTENT_TYPE, "application/json")
                    .setStatusCode(500)
                    .end(VertxJsonException.string());
        }
    }

    public static void handle(Throwable e, WebSocketRoutingContext ctx) {
        if (e instanceof VertxJsonException error) {
            ctx.response()
                    .setStatusCode(error.statusCode)
                    .error(error);
        } else {
            ctx.response()
                    .setStatusCode(502)
                    .error(VertxJsonException.string());
        }
    }
}
