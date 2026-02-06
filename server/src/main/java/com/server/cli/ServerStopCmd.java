package com.server.cli;

import com.server.launcher.Application;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.Optional;

public record ServerStopCmd(
        Application app,
        Vertx vertx
) implements ICliCommand {

    @Override
    public Optional<JsonObject> execute() throws Exception {
        if (app.isRunning()) {
            vertx.undeploy(app.deploymentID());

            vertx.setTimer(500, tid -> System.exit(0));

            return Optional.of(new JsonObject());
        }

        throw new Exception("Server application is not running");
    }

}
