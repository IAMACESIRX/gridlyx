package com.example.examplemod.advanced.sandbox;

// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/physics/PhysicsWorld.java
import com.example.examplemod.advanced.physics.PhysicsWorld.Vec3;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ToolGunController {
    private final Map<String, ToolMode> modes = new ConcurrentHashMap<>();

    public void register(String id, ToolMode mode) {
        modes.put(id, mode);
    }

    public ToolResult fire(
            String modeId,
            RaycastWorld world,
            Vec3 origin,
            Vec3 direction,
            double range,
            ToolContext context) {
        ToolMode mode = modes.get(modeId);
        if (mode == null) {
            return ToolResult.failure("Unknown tool mode: " + modeId);
        }
        RayHit hit = world.raycast(origin, direction.normalized(), range);
        if (hit == null) {
            return ToolResult.failure("No construction target");
        }
        return mode.apply(hit, context);
    }

    @FunctionalInterface
    public interface RaycastWorld {
        RayHit raycast(Vec3 origin, Vec3 direction, double range);
    }

    @FunctionalInterface
    public interface ToolMode {
        ToolResult apply(RayHit hit, ToolContext context);
    }

    public record RayHit(long targetId, Vec3 point, Vec3 normal, double distance) {}

    public record ToolContext(long actorId, Map<String, Object> options) {}

    public record ToolResult(boolean success, String message) {
        public static ToolResult success(String message) {
            return new ToolResult(true, message);
        }

        public static ToolResult failure(String message) {
            return new ToolResult(false, message);
        }
    }
}
