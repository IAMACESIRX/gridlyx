package com.example.examplemod.advanced.render;

public final class TwoBoneIkSolver {
    private TwoBoneIkSolver() {}

    public static Solution solve(double upperLength, double lowerLength, double targetDistance) {
        if (upperLength <= 0 || lowerLength <= 0) {
            throw new IllegalArgumentException("bone lengths must be positive");
        }
        double min = Math.abs(upperLength - lowerLength) + 1.0e-9;
        double max = upperLength + lowerLength - 1.0e-9;
        double distance = clamp(targetDistance, min, max);
        double shoulder = Math.acos(clamp(
                (upperLength * upperLength + distance * distance - lowerLength * lowerLength)
                        / (2.0 * upperLength * distance),
                -1.0,
                1.0));
        double elbow = Math.acos(clamp(
                (upperLength * upperLength + lowerLength * lowerLength - distance * distance)
                        / (2.0 * upperLength * lowerLength),
                -1.0,
                1.0));
        return new Solution(shoulder, Math.PI - elbow, distance);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public record Solution(double shoulderRadians, double elbowRadians, double solvedDistance) {}
}
