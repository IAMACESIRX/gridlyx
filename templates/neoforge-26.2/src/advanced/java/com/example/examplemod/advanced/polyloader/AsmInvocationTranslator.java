package com.example.examplemod.advanced.polyloader;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class AsmInvocationTranslator {
    public byte[] translate(byte[] source, List<CallTranslationRule> rules) {
        if (rules.isEmpty()) {
            return source;
        }
        AtomicInteger rewrites = new AtomicInteger();
        ClassReader reader = new ClassReader(source);
        ClassWriter writer = new ClassWriter(reader, 0);
        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public MethodVisitor visitMethod(
                    int access,
                    String name,
                    String descriptor,
                    String signature,
                    String[] exceptions) {
                MethodVisitor delegate = super.visitMethod(access, name, descriptor, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM9, delegate) {
                    @Override
                    public void visitMethodInsn(
                            int opcode,
                            String owner,
                            String methodName,
                            String methodDescriptor,
                            boolean isInterface) {
                        for (CallTranslationRule rule : rules) {
                            if (rule.matches(opcode, owner, methodName, methodDescriptor)) {
                                rewrites.incrementAndGet();
                                super.visitMethodInsn(
                                        rule.replacementOpcode(),
                                        rule.replacementOwner(),
                                        rule.replacementName(),
                                        rule.replacementDescriptor(),
                                        rule.replacementInterface());
                                return;
                            }
                        }
                        super.visitMethodInsn(opcode, owner, methodName, methodDescriptor, isInterface);
                    }
                };
            }
        };
        reader.accept(visitor, 0);
        return rewrites.get() == 0 ? source : writer.toByteArray();
    }
}
