package com.server.cli;

import io.vertx.core.json.JsonObject;

import java.util.Optional;

public interface ICliCommand {

    Optional<JsonObject> execute() throws Exception;

}
