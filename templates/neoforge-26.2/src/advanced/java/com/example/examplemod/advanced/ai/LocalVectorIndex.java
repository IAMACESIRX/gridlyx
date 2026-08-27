package com.example.examplemod.advanced.ai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LocalVectorIndex {
    private final ConcurrentHashMap<String, Entry> entries = new ConcurrentHashMap<>();

    public void upsert(String id, float[] vector, Map<String, String> metadata) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Vector document id is required");
        }
        entries.put(id, new Entry(normalize(vector), Map.copyOf(metadata)));
    }

    public boolean remove(String id) {
        return entries.remove(id) != null;
    }

    public int size() {
        return entries.size();
    }

    public List<Hit> search(float[] queryVector, int limit) {
        if (limit <= 0) {
            return List.of();
        }
        float[] query = normalize(queryVector);
        List<Hit> hits = new ArrayList<>(entries.size());
        entries.forEach((id, entry) -> hits.add(new Hit(id, dot(query, entry.vector()), entry.metadata())));
        hits.sort(Comparator.comparingDouble(Hit::score).reversed().thenComparing(Hit::id));
        return List.copyOf(hits.subList(0, Math.min(limit, hits.size())));
    }

    private static float[] normalize(float[] vector) {
        if (vector == null || vector.length == 0) {
            throw new IllegalArgumentException("Vector must contain at least one component");
        }
        double magnitudeSquared = 0.0;
        for (float value : vector) {
            if (!Float.isFinite(value)) {
                throw new IllegalArgumentException("Vector contains a non-finite component");
            }
            magnitudeSquared += (double) value * value;
        }
        if (magnitudeSquared == 0.0) {
            throw new IllegalArgumentException("Zero vectors cannot be indexed");
        }
        double magnitude = Math.sqrt(magnitudeSquared);
        float[] normalized = new float[vector.length];
        for (int index = 0; index < vector.length; index++) {
            normalized[index] = (float) (vector[index] / magnitude);
        }
        return normalized;
    }

    private static double dot(float[] left, float[] right) {
        if (left.length != right.length) {
            return Double.NEGATIVE_INFINITY;
        }
        double result = 0.0;
        for (int index = 0; index < left.length; index++) {
            result += (double) left[index] * right[index];
        }
        return result;
    }

    private record Entry(float[] vector, Map<String, String> metadata) {}

    public record Hit(String id, double score, Map<String, String> metadata) {}
}
