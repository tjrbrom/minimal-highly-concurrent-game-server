package com.server.cli;

import com.server.launcher.Application;
import io.vertx.core.Vertx;

import java.util.Optional;

public final class CliCommandFactory {

    private final Application app;
    private final Vertx vertx;

    public CliCommandFactory(Application app, Vertx vertx) {
        this.app = app;
        this.vertx = vertx;
    }

    public Optional<ICliCommand> create(CliCommandRequest cmd) {
        return switch (cmd.getCommand()) {
            case Stop -> Optional.of(new ServerStopCmd(app, vertx));
            case Status -> Optional.of(new ServerStatusCmd(app));
            case null -> Optional.empty();
        };
    }

}
