package com.example.examplemod.testkit;

import java.time.Duration;
import java.time.Instant;

public final class FakeClock {
    private Instant now;

    public FakeClock(Instant initialTime) {
        now = initialTime;
    }

    public Instant now() {
        return now;
    }

    public void advance(Duration duration) {
        now = now.plus(duration);
    }
}
