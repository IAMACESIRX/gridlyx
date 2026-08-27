package com.example.examplemod.advanced.polyloader;

import java.lang.instrument.Instrumentation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public final class DynamicHandleScanner {
    public List<Binding> scan(
            Instrumentation instrumentation,
            Predicate<Class<?>> ownerFilter,
            MethodShape shape) {
        List<Binding> result = new ArrayList<>();
        for (Class<?> owner : instrumentation.getAllLoadedClasses()) {
            if (!ownerFilter.test(owner)) {
                continue;
            }
            Method[] methods;
            try {
                methods = owner.getDeclaredMethods();
            } catch (RuntimeException | LinkageError ignored) {
                continue;
            }
            for (Method method : methods) {
                int score = shape.score(method);
                if (score < 0) {
                    continue;
                }
                MethodHandle handle = bind(owner, method);
                if (handle != null) {
                    result.add(new Binding(owner, method, handle, score));
                }
            }
        }
        result.sort(Comparator.comparingInt(Binding::score).reversed());
        return List.copyOf(result);
    }

    private static MethodHandle bind(Class<?> owner, Method method) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(owner, MethodHandles.lookup());
            return lookup.unreflect(method);
        } catch (IllegalAccessException | RuntimeException ignored) {
            try {
                return MethodHandles.publicLookup().unreflect(method);
            } catch (IllegalAccessException | RuntimeException secondFailure) {
                return null;
            }
        }
    }

    public enum StaticMode {
        ANY,
        STATIC,
        INSTANCE
    }

    public record MethodShape(
            int parameterCount,
            StaticMode staticMode,
            String returnTypeName,
            List<String> parameterTypeNames) {
        public MethodShape {
            if (parameterCount < -1) {
                throw new IllegalArgumentException("parameterCount must be -1 or greater");
            }
            parameterTypeNames = List.copyOf(parameterTypeNames);
        }

        private int score(Method method) {
            if (parameterCount >= 0 && method.getParameterCount() != parameterCount) {
                return -1;
            }
            boolean isStatic = Modifier.isStatic(method.getModifiers());
            if (staticMode == StaticMode.STATIC && !isStatic) {
                return -1;
            }
            if (staticMode == StaticMode.INSTANCE && isStatic) {
                return -1;
            }
            if (returnTypeName != null && !returnTypeName.equals(method.getReturnType().getName())) {
                return -1;
            }
            if (!parameterTypeNames.isEmpty()) {
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length != parameterTypeNames.size()) {
                    return -1;
                }
                for (int index = 0; index < parameters.length; index++) {
                    String required = parameterTypeNames.get(index);
                    if (required != null && !required.equals(parameters[index].getName())) {
                        return -1;
                    }
                }
            }
            int score = 10;
            score += parameterCount >= 0 ? 10 : 0;
            score += staticMode == StaticMode.ANY ? 0 : 5;
            score += returnTypeName == null ? 0 : 20;
            score += parameterTypeNames.size() * 10;
            return score;
        }
    }

    public record Binding(Class<?> owner, Method method, MethodHandle handle, int score) {}
}
