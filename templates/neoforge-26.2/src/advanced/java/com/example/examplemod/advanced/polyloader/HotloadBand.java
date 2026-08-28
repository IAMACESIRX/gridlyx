package com.example.examplemod.advanced.polyloader;

public enum HotloadBand {
    H0_DATA(0),
    H1_ASSETS(1),
    H2_BEHAVIOR(2),
    H3_MODULE(3),
    H4_REGISTRY(4),
    H5_LIFECYCLE(5),
    H6_EPOCH(6);

    private final int level;

    HotloadBand(int level) {
        this.level = level;
    }

    public int level() {
        return level;
    }

    public boolean atLeast(HotloadBand other) {
        return level >= other.level;
    }
}
