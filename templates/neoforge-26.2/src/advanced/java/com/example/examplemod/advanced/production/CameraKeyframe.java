package com.example.examplemod.advanced.production;

public record CameraKeyframe(RationalTime time, CameraPose pose) implements Comparable<CameraKeyframe> {
    public CameraKeyframe {
        if (time == null || pose == null) {
            throw new IllegalArgumentException("time and pose are required");
        }
    }

    @Override
    public int compareTo(CameraKeyframe other) {
        return time.compareTo(other.time);
    }
}
