package com.server.time;

import java.time.Duration;

public final class Stopwatch {

    private Duration elapsed;
    private boolean paused;

    public Stopwatch() {
        elapsed = Duration.ZERO;
    }

    public Duration elapsed() {
        return elapsed;
    }

    public long elapsedSeconds() {
        return elapsed.toSeconds();
    }

    public long elapsedMilliseconds() {
        return elapsed.toMillis();
    }

    public void setElapsed(Duration duration) {
        this.elapsed = duration;
    }

    public Stopwatch tick(long ns) {
        if (!paused) {
            elapsed = elapsed.plusNanos(ns);
        }
        return this;
    }

    public void pause() {
        paused = true;
    }

    public void unpause() {
        paused = false;
    }

    public boolean paused() {
        return paused;
    }

    public void reset() {
        elapsed = Duration.ZERO;
    }

}
