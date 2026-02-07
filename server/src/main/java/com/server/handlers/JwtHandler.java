package com.server.handlers;

import com.google.inject.Inject;
import io.vertx.core.MultiMap;
import io.vertx.ext.auth.authentication.TokenCredentials;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.AuthenticationHandler;

public final class JwtHandler implements AuthenticationHandler {

    private static final int BEARER_LEN = "Bearer ".length();

    private final JWTAuth jwtAuth;

    @Inject
    public JwtHandler(JWTAuth jwtAuth) {
        this.jwtAuth = jwtAuth;
    }

    @Override
    public void handle(RoutingContext ctx) {
        MultiMap headers = ctx.request().headers();
        if (!headers.contains("Authorization")) {
            ctx.response().setStatusCode(401).end();
            return;
        }

        String token = headers.get("Authorization");
        if (!token.startsWith("Bearer ")) {
            ctx.response().setStatusCode(401).end();
            return;
        }

        token = token.substring(BEARER_LEN);
        jwtAuth.authenticate(new TokenCredentials(token))
                .onComplete(ar ->
                {
                    if (ar.failed()) {
                        ctx.response().setStatusCode(401).end(ar.cause().getMessage());
                        return;
                    }

                    ctx.setUser(ar.result());

                    // continue
                    ctx.next();
                });
    }

}
