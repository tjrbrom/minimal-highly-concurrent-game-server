package com.server.cli;

import com.server.cli.CliCommandRequest;
import com.server.launcher.GameApp;
import io.vertx.core.Vertx;

import java.util.Optional;

public final class CliCommandFactory {

    private final GameApp app;
    private final Vertx vertx;

    public CliCommandFactory(GameApp app, Vertx vertx) {
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
