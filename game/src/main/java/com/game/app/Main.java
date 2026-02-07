package com.game.app;

import com.game.db.PGDatabase;
import com.server.launcher.GameLauncher;
import com.server.mechanics.database.GameDatabaseConfig;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        GameDatabaseConfig config = new GameDatabaseConfig(
            "localhost:5438/game-db",
            "game",
            "password",
            10
        );

        Vertx vertx = vertx();

        PGDatabase db = PGDatabase.initialize(vertx, 5438, "localhost", "game-db", "game", "password");

        GameLauncher launcher = new GameLauncher("config/settings.json",
            Runtime.getRuntime().availableProcessors(),
            vertx,
            List.of(Main.class.getPackageName()),
            new GameApplication(),
            List.of(new GameRouter()),
            List.of()
        );

        launcher.start();
    }

    private static Vertx vertx() {
        VertxOptions vertxOptions = new VertxOptions()
                .setEventLoopPoolSize(20)
                .setWorkerPoolSize(20)
                .setInternalBlockingPoolSize(5)
                .setBlockedThreadCheckInterval(TimeUnit.MINUTES.toMillis(2L));

        return Vertx.vertx(vertxOptions);
    }
}
