package com.server.launcher;

import com.server.time.Stopwatch;
import io.vertx.core.AbstractVerticle;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

public abstract class GameApp extends AbstractVerticle {

    public final String name;

    @Getter
    protected LocalDateTime startTime;

    protected final Stopwatch elapsedTime;

    @Setter
    @Getter
    protected long period;

    @Getter
    protected long previousNanosecond;

    @Getter
    protected long currentNanosecond = 0;

    protected long deltaTime = 0;

    @Getter
    private boolean running;

    protected GameApp(String name, long initialMillisecond) {
        this.name = name;
        this.period = TimeUnit.MILLISECONDS.toMillis(500);
        this.previousNanosecond = TimeUnit.MILLISECONDS.toNanos(initialMillisecond);
        elapsedTime = new Stopwatch();
    }

    public abstract void mainHandler();

    public void onStart() {
    }

    public void onShutdown() {
    }

    /**
     * Overrides the Runnable interface. Avoid overriding this method, and instead utilize the mainHandler, unless
     * there is a reason to do it.
     */
    public void run() {
        calculateDeltaTime();
        mainHandler();
        previousNanosecond = currentNanosecond;
    }


    public void loop() {
        if (running) {
            run();
            vertx.setTimer(period, tid -> loop());
        }
    }

    @Override
    public void start() {
        running = true;
        onStart();
        startTime = LocalDateTime.now(ZoneOffset.UTC);
        elapsedTime.reset();
        vertx.setTimer(1, tid -> loop());
    }

    @Override
    public void stop() {
        if (running) {
            running = false;
            onShutdown();
        }
    }

    public void setPeriodByTicks(int ticks) {
        this.period = 1000 / ticks;
    }

    public Duration elapsedTime() {
        return this.elapsedTime.elapsed();
    }

    /**
     * Gets the new nano time (currentNanosecond), calculates the delta time,
     * and then increments the elapsed time with the delta time
     * */
    protected void calculateDeltaTime() {
        currentNanosecond = System.nanoTime();
        deltaTime = currentNanosecond - previousNanosecond;
        elapsedTime.tick(deltaTime);
    }
}
