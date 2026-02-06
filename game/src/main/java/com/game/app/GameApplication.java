package com.game.app;

import com.server.launcher.Application;

import java.util.concurrent.TimeUnit;

public final class GameApplication extends Application {

    public GameApplication() {
        super("game", System.nanoTime());
        setPeriod(TimeUnit.MILLISECONDS.toMillis(15));
        previousNanosecond = System.nanoTime();
    }

    @Override
    public void mainHandler() {
        // TODO: implement
    }

}
