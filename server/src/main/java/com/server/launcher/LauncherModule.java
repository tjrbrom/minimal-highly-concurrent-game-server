package com.server.launcher;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.server.handlers.JwtHandler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.sstore.SessionStore;

public final class LauncherModule implements Module {

    private final Vertx vertx;
    private final JsonObject configuration;
    private final JwtHandler jwtHandler;
    private final SessionStore sessionStore;

    public LauncherModule(Vertx vertx,
                          JsonObject configuration,
                          JwtHandler jwtHandler,
                          SessionStore sessionStore) {
        this.vertx = vertx;
        this.configuration = configuration;
        this.jwtHandler = jwtHandler;
        this.sessionStore = sessionStore;
    }

    @Override
    public void configure(Binder binder) {
        binder.bind(Vertx.class).toInstance(vertx);
        binder.bind(JsonObject.class).toInstance(configuration);
        binder.bind(JwtHandler.class).toInstance(jwtHandler);
        binder.bind(SessionStore.class).toInstance(sessionStore);
    }

}
