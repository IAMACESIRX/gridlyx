package com.example.examplemod.advanced.scene;

import java.util.Map;
import java.util.UUID;

public final class GizmoManipulator {
    public SceneGraph.Node apply(SceneGraph graph, UUID nodeId, Operation operation, double a, double b, double c) {
        SceneGraph.Node node = graph.get(nodeId);
        if (node == null) {
            throw new IllegalArgumentException("Scene node does not exist");
        }
        Transform3d current = node.transform();
        Transform3d next = switch (operation) {
            case TRANSLATE -> new Transform3d(
                    current.x() + a, current.y() + b, current.z() + c,
                    current.pitch(), current.yaw(), current.roll(),
                    current.scaleX(), current.scaleY(), current.scaleZ());
            case ROTATE -> new Transform3d(
                    current.x(), current.y(), current.z(),
                    current.pitch() + a, current.yaw() + b, current.roll() + c,
                    current.scaleX(), current.scaleY(), current.scaleZ());
            case SCALE -> new Transform3d(
                    current.x(), current.y(), current.z(),
                    current.pitch(), current.yaw(), current.roll(),
                    current.scaleX() * a, current.scaleY() * b, current.scaleZ() * c);
        };
        return graph.update(nodeId, next, Map.copyOf(node.properties()));
    }

    public enum Operation {
        TRANSLATE,
        ROTATE,
        SCALE
    }
}
