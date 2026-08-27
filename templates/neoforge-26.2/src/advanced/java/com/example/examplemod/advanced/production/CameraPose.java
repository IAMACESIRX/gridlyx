package com.example.examplemod.advanced.production;

public record CameraPose(
        double x,
        double y,
        double z,
        double yawDegrees,
        double pitchDegrees,
        double rollDegrees,
        double fieldOfViewDegrees,
        double focusDistance) {

    public static CameraPose interpolate(CameraPose from, CameraPose to, double alpha) {
        double t = Math.max(0.0, Math.min(1.0, alpha));
        return new CameraPose(
                lerp(from.x, to.x, t),
                lerp(from.y, to.y, t),
                lerp(from.z, to.z, t),
                lerpAngle(from.yawDegrees, to.yawDegrees, t),
                lerpAngle(from.pitchDegrees, to.pitchDegrees, t),
                lerpAngle(from.rollDegrees, to.rollDegrees, t),
                lerp(from.fieldOfViewDegrees, to.fieldOfViewDegrees, t),
                lerp(from.focusDistance, to.focusDistance, t));
    }

    private static double lerp(double from, double to, double t) {
        return from + (to - from) * t;
    }

    private static double lerpAngle(double from, double to, double t) {
        double delta = ((to - from + 540.0) % 360.0) - 180.0;
        return from + delta * t;
    }
}
