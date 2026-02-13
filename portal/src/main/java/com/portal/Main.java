package com.portal;

import com.portal.security.Argon2;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.PubSecKeyOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

    public static void main(String[] args) {

        Argon2.initialize();

        VertxOptions options = new VertxOptions();
        Vertx vertx = Vertx.vertx(options);

        configuration(vertx)
                .compose(configuration -> createJWTAuthOptions(vertx, configuration)
                        .compose(jwtOptions -> {
                            JWTAuth jwtAuth = JWTAuth.create(vertx, jwtOptions);

                            // TODO: implement

                            return Future.succeededFuture(configuration);
                        }))
                .onSuccess(configuration -> {
                    vertx.deployVerticle(() -> new HttpVerticle(1234, null, true), new DeploymentOptions()
                                    .setInstances(1))
                            .onSuccess(Void -> {
                                // TODO: implement
                            })
                            .onFailure(ex -> log.error("Failed to start http server", ex));
                });
    }

    private static Future<JsonObject> configuration(Vertx vertx) {
        String filePath = (String) System.getProperties().getOrDefault("portal.config", "config/settings.json");
        ConfigStoreOptions fileStore = new ConfigStoreOptions()
                .setType("file")
                .setFormat("json")
                .setConfig(new JsonObject().put("path", filePath));

        ConfigRetrieverOptions options = new ConfigRetrieverOptions()
                .addStore(fileStore);

        ConfigRetriever retriever = ConfigRetriever.create(vertx, options);

        retriever.listen(change -> log.info("Config file was changed"));

        return retriever.getConfig();
    }

    private static Future<JWTAuthOptions> createJWTAuthOptions(Vertx vertx, JsonObject config) {
        if (!config.containsKey("keys")) {
            return Future.failedFuture(new Exception("Missing rsa keys"));
        }

        JsonObject keyConfig = config.getJsonObject("keys");
        if (!keyConfig.containsKey("public")) {
            return Future.failedFuture(new Exception("Missing public key in config"));
        } else if (!keyConfig.containsKey("private")) {
            return Future.failedFuture(new Exception("Missing private key in config"));
        }

        Future<Buffer> privateKey = vertx.fileSystem().readFile(keyConfig.getString("private"));
        Future<Buffer> publicKey = vertx.fileSystem().readFile(keyConfig.getString("public"));

        return Future.all(privateKey, publicKey)
                .compose(ar -> {
                    JWTAuthOptions options = new JWTAuthOptions()
                            .addPubSecKey(new PubSecKeyOptions()
                                    .setAlgorithm("RS256")
                                    .setBuffer(publicKey.result()))
                            .addPubSecKey(new PubSecKeyOptions()
                                    .setAlgorithm("RS256")
                                    .setBuffer(privateKey.result()));
                    return Future.succeededFuture(options);
                });
    }

}
