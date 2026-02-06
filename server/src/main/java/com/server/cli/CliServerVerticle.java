package com.server.cli;

import com.server.handlers.BasicCredentialHandler;
import com.server.handlers.LoggerHandler;
import com.server.handlers.VertxExceptionHandler;
import com.server.launcher.Application;
import com.server.util.json.VertxJsonResponse;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public final class CliServerVerticle extends AbstractVerticle {

    private final CliCommandFactory cmdFactory;
    private final Router router;
    private final int port;

    @SneakyThrows
    public CliServerVerticle(Vertx vertx,
                             Application app,
                             int port,
                             String username,
                             String password) {
        cmdFactory = new CliCommandFactory(app, vertx);
        this.port = port;
        router = Router.router(vertx);
        router.route().handler(new BasicCredentialHandler(username, password));
        router.route().handler(new LoggerHandler());
        router.route().handler(BodyHandler.create());
        router.post("cli/cmd").handler(this::cliCommand);
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        HttpServerOptions options = new HttpServerOptions()
                .setLogActivity(true);

        vertx.createHttpServer(options)
                .requestHandler(router)
                .listen(port)
                .onSuccess(Void -> {
                    startPromise.complete();
                    log.info("CLI Server is running at localhost: {} ", port);
                })
                .onFailure(startPromise::fail);
    }

    private void cliCommand(RoutingContext context) throws Exception {
        CliCommandRequest cmdRequest = context.body().asJsonObject().mapTo(CliCommandRequest.class);
        Optional<ICliCommand> cliCommand = cmdFactory.create(cmdRequest);
        if (cliCommand.isEmpty()) {
            VertxExceptionHandler.handle(new Exception("Command not found"), context);
        } else {
            VertxJsonResponse.endWithResult(context, 200, cliCommand.get().execute());
        }
    }

}
