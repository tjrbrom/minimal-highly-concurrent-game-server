package com.server.handlers;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

public final class BasicCredentialHandler implements Handler<RoutingContext> {

    private static final String AUTH_PREFIX = "Basic ";

    private final byte[] expectedUsername;
    private final byte[] expectedPassword;

    public BasicCredentialHandler(String username, String password) {
        this.expectedUsername = username.getBytes(StandardCharsets.UTF_8);
        this.expectedPassword = password.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void handle(RoutingContext ctx) {

        String auth = ctx.request().getHeader("Authorization");
        if (auth == null || !auth.startsWith(AUTH_PREFIX)) {
            reject(ctx);
            return;
        }

        try {
            byte[] decoded = Base64.getDecoder()
                    .decode(auth.substring(AUTH_PREFIX.length()));

            String[] parts = new String(decoded, StandardCharsets.UTF_8).split(":", 2);
            if (parts.length != 2) {
                reject(ctx);
                return;
            }

            byte[] user = parts[0].getBytes(StandardCharsets.UTF_8);
            byte[] pass = parts[1].getBytes(StandardCharsets.UTF_8);

            if (!MessageDigest.isEqual(expectedUsername, user) ||
                    !MessageDigest.isEqual(expectedPassword, pass)) {
                reject(ctx);
                return;
            }

            ctx.next();

        } catch (IllegalArgumentException e) {
            reject(ctx);
        }
    }

    private static void reject(RoutingContext ctx) {
        ctx.response()
                .putHeader("WWW-Authenticate", "Basic")
                .setStatusCode(401)
                .end();
    }
}
