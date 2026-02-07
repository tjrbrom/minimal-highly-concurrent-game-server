package com.server.http;

import com.server.websocket.handler.WebSocketHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class HttpVerticle extends AbstractVerticle {

    private final int port;
    private final Router router;
    private final WebSocketHandler webSocketHandler;

    public HttpVerticle(int port, Router router,
                        WebSocketHandler webSocketHandler) {
        this.port = port;
        this.router = router;
        this.webSocketHandler = webSocketHandler;
    }

    @Override
    public void start(Promise<Void> startPromise) {
        HttpServerOptions options = new HttpServerOptions()
                .setLogActivity(true);

        HttpServer httpServer = vertx.createHttpServer(options)
                .requestHandler(this::requestHandler);

        if (webSocketHandler != null)
            httpServer.webSocketHandler(webSocketHandler);

        httpServer
                .listen(port)
                .compose(server -> Future.succeededFuture())
                .onSuccess(Void -> {
                    startPromise.complete();
                })
                .onFailure(startPromise::fail);
    }

    private void requestHandler(HttpServerRequest request) {
        router.handle(request);
    }
}
