package com.example.examplemod.advanced.scene;

public record Transform3d(
        double x,
        double y,
        double z,
        double pitch,
        double yaw,
        double roll,
        double scaleX,
        double scaleY,
        double scaleZ) {
    public Transform3d {
        if (!finite(x, y, z, pitch, yaw, roll, scaleX, scaleY, scaleZ)) {
            throw new IllegalArgumentException("Transform values must be finite");
        }
        if (scaleX == 0.0 || scaleY == 0.0 || scaleZ == 0.0) {
            throw new IllegalArgumentException("Scale cannot be zero");
        }
    }

    public static Transform3d identity() {
        return new Transform3d(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
    }

    private static boolean finite(double... values) {
        for (double value : values) {
            if (!Double.isFinite(value)) {
                return false;
            }
        }
        return true;
    }
}
