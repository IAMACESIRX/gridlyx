package com.example.examplemod.advanced.production;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CameraTrack {
    private final List<CameraKeyframe> keyframes;

    public CameraTrack(List<CameraKeyframe> keyframes) {
        if (keyframes == null || keyframes.isEmpty()) {
            throw new IllegalArgumentException("camera track requires at least one keyframe");
        }
        ArrayList<CameraKeyframe> sorted = new ArrayList<>(keyframes);
        Collections.sort(sorted);
        for (int i = 1; i < sorted.size(); i++) {
            if (sorted.get(i - 1).time().compareTo(sorted.get(i).time()) == 0) {
                throw new IllegalArgumentException("camera keyframes may not share the same time");
            }
        }
        this.keyframes = List.copyOf(sorted);
    }

    public List<CameraKeyframe> keyframes() {
        return keyframes;
    }

    public CameraPose sample(RationalTime time) {
        if (time.compareTo(keyframes.getFirst().time()) <= 0) {
            return keyframes.getFirst().pose();
        }
        if (time.compareTo(keyframes.getLast().time()) >= 0) {
            return keyframes.getLast().pose();
        }
        for (int i = 1; i < keyframes.size(); i++) {
            CameraKeyframe next = keyframes.get(i);
            if (time.compareTo(next.time()) <= 0) {
                CameraKeyframe previous = keyframes.get(i - 1);
                double span = next.time().seconds() - previous.time().seconds();
                double alpha = (time.seconds() - previous.time().seconds()) / span;
                return CameraPose.interpolate(previous.pose(), next.pose(), alpha);
            }
        }
        throw new IllegalStateException("camera sampling fell outside keyframe range");
    }
}
