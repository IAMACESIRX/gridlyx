package com.example.examplemod.advanced.production;

public record RationalTime(long numerator, long denominator) implements Comparable<RationalTime> {
    public RationalTime {
        if (denominator == 0) {
            throw new IllegalArgumentException("denominator must not be zero");
        }
        if (denominator < 0) {
            numerator = -numerator;
            denominator = -denominator;
        }
        long gcd = gcd(Math.abs(numerator), denominator);
        numerator /= gcd;
        denominator /= gcd;
    }

    public static RationalTime frames(long frame, long framesPerSecond) {
        if (framesPerSecond <= 0) {
            throw new IllegalArgumentException("framesPerSecond must be positive");
        }
        return new RationalTime(frame, framesPerSecond);
    }

    public double seconds() {
        return (double) numerator / denominator;
    }

    @Override
    public int compareTo(RationalTime other) {
        return Long.compare(Math.multiplyExact(numerator, other.denominator),
                Math.multiplyExact(other.numerator, denominator));
    }

    private static long gcd(long left, long right) {
        while (right != 0) {
            long next = left % right;
            left = right;
            right = next;
        }
        return left == 0 ? 1 : left;
    }
}
