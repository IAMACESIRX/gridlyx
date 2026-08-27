package com.example.examplemod.advanced.bytecode;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class AsmClassGenerator {
    private AsmClassGenerator() {}

    public static byte[] generateProbe(String internalName) {
        if (!internalName.matches("[A-Za-z_$][A-Za-z0-9_$]*(/[A-Za-z_$][A-Za-z0-9_$]*)*")) {
            throw new IllegalArgumentException("Invalid JVM internal class name");
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        writer.visit(Opcodes.V25, Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, internalName, null, "java/lang/Object", null);

        MethodVisitor constructor = writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(0, 0);
        constructor.visitEnd();

        MethodVisitor ping = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "ping", "()Ljava/lang/String;", null, null);
        ping.visitCode();
        ping.visitLdcInsn("ok");
        ping.visitInsn(Opcodes.ARETURN);
        ping.visitMaxs(0, 0);
        ping.visitEnd();

        writer.visitEnd();
        return writer.toByteArray();
    }
}
