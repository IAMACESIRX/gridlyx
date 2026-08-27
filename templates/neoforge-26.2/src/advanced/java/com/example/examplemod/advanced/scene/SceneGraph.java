package com.example.examplemod.advanced.scene;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public final class SceneGraph {
    private final Map<UUID, Node> nodes = new ConcurrentHashMap<>();
    private final AtomicLong revision = new AtomicLong();

    public Node create(UUID parent, String type, Transform3d transform, Map<String, Object> properties) {
        if (parent != null && !nodes.containsKey(parent)) {
            throw new IllegalArgumentException("Parent does not exist");
        }
        Node node = new Node(UUID.randomUUID(), parent, type, transform, properties, revision.incrementAndGet());
        nodes.put(node.id(), node);
        return node;
    }

    public Node update(UUID id, Transform3d transform, Map<String, Object> properties) {
        return nodes.compute(id, (key, current) -> {
            if (current == null) {
                throw new IllegalArgumentException("Scene node does not exist");
            }
            return new Node(current.id(), current.parent(), current.type(), transform, properties,
                    revision.incrementAndGet());
        });
    }

    public void removeRecursive(UUID id) {
        List<UUID> children = childrenOf(id).stream().map(Node::id).toList();
        for (UUID child : children) {
            removeRecursive(child);
        }
        nodes.remove(id);
        revision.incrementAndGet();
    }

    public Node get(UUID id) {
        return nodes.get(id);
    }

    public List<Node> childrenOf(UUID parent) {
        List<Node> children = new ArrayList<>();
        for (Node node : nodes.values()) {
            if (Objects.equals(node.parent(), parent)) {
                children.add(node);
            }
        }
        return List.copyOf(children);
    }

    public long revision() {
        return revision.get();
    }

    public record Node(
            UUID id,
            UUID parent,
            String type,
            Transform3d transform,
            Map<String, Object> properties,
            long revision) {
        public Node {
            type = Objects.requireNonNull(type);
            transform = Objects.requireNonNull(transform);
            properties = Map.copyOf(properties);
        }
    }
}
