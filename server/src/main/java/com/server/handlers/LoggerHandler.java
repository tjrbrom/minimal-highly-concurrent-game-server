package com.server.handlers;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class LoggerHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        final HttpServerRequest request = ctx.request();
        log.info("{} {} - {}",
                request.method().toString(),
                request.absoluteURI(),
                request.remoteAddress().hostAddress());
        ctx.next();
    }

}
