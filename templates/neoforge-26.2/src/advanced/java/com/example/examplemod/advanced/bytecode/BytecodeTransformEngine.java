package com.example.examplemod.advanced.bytecode;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public final class BytecodeTransformEngine implements ClassFileTransformer {
    private final List<Rule> rules = new CopyOnWriteArrayList<>();

    public void addRule(Predicate<String> classNameFilter, BiFunction<String, byte[], byte[]> transformer) {
        rules.add(new Rule(classNameFilter, transformer));
    }

    @Override
    public byte[] transform(
            Module module,
            ClassLoader loader,
            String className,
            Class<?> classBeingRedefined,
            ProtectionDomain protectionDomain,
            byte[] classfileBuffer) {
        if (className == null) {
            return null;
        }
        byte[] current = classfileBuffer;
        boolean changed = false;
        for (Rule rule : rules) {
            if (rule.classNameFilter().test(className)) {
                byte[] next = rule.transformer().apply(className, current);
                if (next != null && next != current) {
                    current = next;
                    changed = true;
                }
            }
        }
        return changed ? current : null;
    }

    private record Rule(Predicate<String> classNameFilter, BiFunction<String, byte[], byte[]> transformer) {}
}
