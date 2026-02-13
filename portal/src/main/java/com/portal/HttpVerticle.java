package com.portal;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HttpVerticle extends AbstractVerticle {

    private final int port;
    private final Router router;
    private final boolean logging;

    public HttpVerticle(int port, Router router, boolean logging) {
        this.port = port;
        this.router = router;
        this.logging = logging;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        HttpServerOptions options = new HttpServerOptions()
                .setLogActivity(true);

        vertx.createHttpServer(options)
                .requestHandler(this::requestHandler)
                .listen(port)
                .onSuccess(server -> {
                    startPromise.complete();
                    if (logging)
                        log.info("Server running at http://localhost:{}", port);
                })
                .onFailure(startPromise::fail);
    }

    private void requestHandler(HttpServerRequest request) {
        router.handle(request);
    }

}
