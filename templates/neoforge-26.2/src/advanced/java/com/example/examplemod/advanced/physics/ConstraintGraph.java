package com.example.examplemod.advanced.physics;

import com.example.examplemod.advanced.physics.PhysicsWorld.Body;
import com.example.examplemod.advanced.physics.PhysicsWorld.Vec3;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class ConstraintGraph {
    private final Map<Long, DistanceConstraint> constraints = new ConcurrentHashMap<>();
    private final AtomicLong nextId = new AtomicLong(1L);

    public long addDistance(long firstBody, long secondBody, double restLength, double stiffness) {
        if (restLength < 0.0 || stiffness < 0.0 || stiffness > 1.0) {
            throw new IllegalArgumentException("Invalid distance-constraint parameters");
        }
        long id = nextId.getAndIncrement();
        constraints.put(id, new DistanceConstraint(firstBody, secondBody, restLength, stiffness));
        return id;
    }

    public boolean remove(long constraintId) {
        return constraints.remove(constraintId) != null;
    }

    public void solve(PhysicsWorld world, int iterations) {
        for (int iteration = 0; iteration < iterations; iteration++) {
            for (DistanceConstraint constraint : constraints.values()) {
                solveDistance(world, constraint);
            }
        }
    }

    private static void solveDistance(PhysicsWorld world, DistanceConstraint constraint) {
        Body first = world.body(constraint.firstBody());
        Body second = world.body(constraint.secondBody());
        Vec3 delta = second.position().subtract(first.position());
        double distance = delta.length();
        if (distance == 0.0) {
            return;
        }
        double error = distance - constraint.restLength();
        Vec3 correction = delta.normalized().scale(error * constraint.stiffness() * 0.5);
        first.translate(correction);
        second.translate(correction.scale(-1.0));
    }

    private record DistanceConstraint(
            long firstBody,
            long secondBody,
            double restLength,
            double stiffness) {}
}
