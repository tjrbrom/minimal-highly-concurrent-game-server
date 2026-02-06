package com.server.cli;

import com.server.launcher.Application;
import io.vertx.core.json.JsonObject;

import java.util.Optional;

public record ServerStatusCmd(
        Application app
) implements ICliCommand {

    @Override
    public Optional<JsonObject> execute() {
        final JsonObject jsonObject;
        if (app.isRunning()) {
            jsonObject = new JsonObject()
                    .put("status", "running")
                    .put("period", app.getPeriod())
                    .put("nanosecond", app.getCurrentNanosecond());
        } else {
            jsonObject = new JsonObject()
                    .put("status", "stopped")
                    .put("period", app.getPeriod())
                    .put("nanosecond", app.getCurrentNanosecond());
        }
        return Optional.of(jsonObject);
    }

}
