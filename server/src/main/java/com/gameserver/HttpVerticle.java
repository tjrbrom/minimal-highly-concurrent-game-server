package com.gameserver;

import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

/**
 * Simple HTTP verticle responsible for exposing basic HTTP endpoints.
 *
 * <p>
 * This verticle is deployed with multiple instances to take advantage of
 * available CPU cores. Each instance creates its own HTTP server and router,
 * while Vert.x internally handles connection distribution.
 * </p>
 *
 * <p>
 * The verticle must remain non-blocking. Any long-running or blocking work
 * should be offloaded to worker verticles or dedicated executors.
 * </p>
 */
@Slf4j
public class HttpVerticle extends AbstractVerticle {

    private final int port;

    public HttpVerticle(int port) {
        this.port = port;
    }

    @Override
    public void start() {

        Router router = Router.router(vertx);

        router.get("/health").handler(ctx -> ctx.response().end("OK"));

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(port)
                .onSuccess(server -> log.info("HTTP server listening on " + port))
                .onFailure(ex -> log.error("Failed to start HTTP server", ex));
    }
}
