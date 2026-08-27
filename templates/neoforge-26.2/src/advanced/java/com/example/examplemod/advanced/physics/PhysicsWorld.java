package com.example.examplemod.advanced.physics;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PhysicsWorld {
    private final Map<Long, Body> bodies = new ConcurrentHashMap<>();

    public Body create(long id, double mass, Vec3 position) {
        if (mass <= 0.0 || !Double.isFinite(mass)) {
            throw new IllegalArgumentException("Physics body mass must be finite and positive");
        }
        Body body = new Body(id, mass, position, Vec3.ZERO, Vec3.ZERO);
        Body previous = bodies.putIfAbsent(id, body);
        if (previous != null) {
            throw new IllegalArgumentException("Duplicate physics body id: " + id);
        }
        return body;
    }

    public Body body(long id) {
        Body body = bodies.get(id);
        if (body == null) {
            throw new IllegalArgumentException("Unknown physics body: " + id);
        }
        return body;
    }

    public Collection<Body> bodies() {
        return bodies.values();
    }

    public void applyForce(long id, Vec3 force) {
        body(id).addForce(force);
    }

    public void step(double deltaSeconds, Vec3 gravity) {
        if (!(deltaSeconds > 0.0) || !Double.isFinite(deltaSeconds)) {
            throw new IllegalArgumentException("Physics step must be finite and positive");
        }
        for (Body body : bodies.values()) {
            body.integrate(deltaSeconds, gravity);
        }
    }

    public static final class Body {
        private final long id;
        private final double mass;
        private Vec3 position;
        private Vec3 velocity;
        private Vec3 accumulatedForce;

        private Body(long id, double mass, Vec3 position, Vec3 velocity, Vec3 force) {
            this.id = id;
            this.mass = mass;
            this.position = position;
            this.velocity = velocity;
            accumulatedForce = force;
        }

        public synchronized long id() {
            return id;
        }

        public synchronized double mass() {
            return mass;
        }

        public synchronized Vec3 position() {
            return position;
        }

        public synchronized Vec3 velocity() {
            return velocity;
        }

        public synchronized void setVelocity(Vec3 nextVelocity) {
            velocity = nextVelocity;
        }

        public synchronized void translate(Vec3 delta) {
            position = position.add(delta);
        }

        private synchronized void addForce(Vec3 force) {
            accumulatedForce = accumulatedForce.add(force);
        }

        private synchronized void integrate(double deltaSeconds, Vec3 gravity) {
            Vec3 acceleration = gravity.add(accumulatedForce.scale(1.0 / mass));
            velocity = velocity.add(acceleration.scale(deltaSeconds));
            position = position.add(velocity.scale(deltaSeconds));
            accumulatedForce = Vec3.ZERO;
        }
    }

    public record Vec3(double x, double y, double z) {
        public static final Vec3 ZERO = new Vec3(0.0, 0.0, 0.0);

        public Vec3 add(Vec3 other) {
            return new Vec3(x + other.x, y + other.y, z + other.z);
        }

        public Vec3 subtract(Vec3 other) {
            return new Vec3(x - other.x, y - other.y, z - other.z);
        }

        public Vec3 scale(double scalar) {
            return new Vec3(x * scalar, y * scalar, z * scalar);
        }

        public double length() {
            return Math.sqrt(x * x + y * y + z * z);
        }

        public Vec3 normalized() {
            double length = length();
            return length == 0.0 ? ZERO : scale(1.0 / length);
        }
    }
}
