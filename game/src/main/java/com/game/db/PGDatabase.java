package com.game.db;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.pgclient.PgBuilder;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.sqlclient.Pool;
import io.vertx.sqlclient.PoolOptions;

import java.util.concurrent.ExecutionException;

public final class PGDatabase {

    private final Pool pool;

    public PGDatabase(Pool pool) {
        this.pool = pool;
    }

    public static PGDatabase initialize(Vertx vertx, int port, String host, String database, String user, String password) throws ExecutionException, InterruptedException {
        PgConnectOptions connectOptions = new PgConnectOptions()
                .setPort(port)
                .setHost(host)
                .setDatabase(database)
                .setUser(user)
                .setPassword(password);

        PoolOptions poolOptions = new PoolOptions()
                .setMaxSize(5);

        Pool pool = PgBuilder
                .pool()
                .with(poolOptions)
                .connectingTo(connectOptions)
                .using(vertx)
                .build();

        testConnection(pool).toCompletionStage().toCompletableFuture().get();
        return new PGDatabase(pool);
    }

    private static Future<Void> testConnection(Pool pool) {
        return pool.preparedQuery("SELECT 1")
                .execute()
                .compose(rows -> Future.succeededFuture());
    }
}
