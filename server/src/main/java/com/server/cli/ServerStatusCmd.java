package com.server.cli;

import com.server.launcher.GameApp;
import io.vertx.core.json.JsonObject;

import java.util.Optional;

public record ServerStatusCmd(
        GameApp app
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
