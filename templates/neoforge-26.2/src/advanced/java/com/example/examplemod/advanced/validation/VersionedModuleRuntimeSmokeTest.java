package com.example.examplemod.advanced.validation;

import com.example.examplemod.advanced.polyloader.ActivationStrategy;
import com.example.examplemod.advanced.polyloader.GridelyxHotloadModule;
import com.example.examplemod.advanced.polyloader.ModuleScope;
import com.example.examplemod.advanced.polyloader.VersionedModuleRuntime;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/** Executable H3 classloader-epoch smoke validation. */
public final class VersionedModuleRuntimeSmokeTest {
    private static final String IMPLEMENTATION = "gridelyx.smoke.TestHotloadModule";

    private VersionedModuleRuntimeSmokeTest() {}

    public static void main(String[] args) throws Exception {
        Path root = Files.createTempDirectory("gridelyx-module-runtime-");
        Path jar = root.resolve("module.jar");
        try {
            createServiceJar(jar);
            try (VersionedModuleRuntime runtime = new VersionedModuleRuntime(
                    VersionedModuleRuntimeSmokeTest.class.getClassLoader())) {
                ActivationStrategy loaded = runtime.reload(jar);
                require(
                        loaded == ActivationStrategy.CLASSLOADER_EPOCH,
                        "H3 module did not use classloader epoch");
                require(runtime.activeModuleCount() == 1, "H3 module did not become active");

                ActivationStrategy removed = runtime.remove(jar);
                require(
                        removed == ActivationStrategy.CLASSLOADER_EPOCH,
                        "H3 module removal changed strategy");
                require(runtime.activeModuleCount() == 0, "H3 module was not retired");
            }
            System.out.println("PASS: Gridelyx H3 versioned module runtime smoke test");
        } finally {
            deleteTree(root);
        }
    }

    private static void createServiceJar(Path jar) throws IOException {
        byte[] implementation = implementationBytecode();
        String classEntry = IMPLEMENTATION.replace('.', '/') + ".class";
        String serviceEntry = "META-INF/services/" + GridelyxHotloadModule.class.getName();

        try (OutputStream output = Files.newOutputStream(jar);
                JarOutputStream archive = new JarOutputStream(output)) {
            archive.putNextEntry(new JarEntry(classEntry));
            archive.write(implementation);
            archive.closeEntry();

            archive.putNextEntry(new JarEntry(serviceEntry));
            archive.write((IMPLEMENTATION + "\n").getBytes(StandardCharsets.UTF_8));
            archive.closeEntry();
        }
    }

    private static byte[] implementationBytecode() {
        String implementation = IMPLEMENTATION.replace('.', '/');
        String contract = Type.getInternalName(GridelyxHotloadModule.class);
        String scope = Type.getDescriptor(ModuleScope.class);
        ClassWriter writer = new ClassWriter(0);
        writer.visit(
                Opcodes.V17,
                Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL,
                implementation,
                null,
                "java/lang/Object",
                new String[] {contract});

        MethodVisitor constructor =
                writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "java/lang/Object",
                "<init>",
                "()V",
                false);
        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(1, 1);
        constructor.visitEnd();

        MethodVisitor moduleId = writer.visitMethod(
                Opcodes.ACC_PUBLIC,
                "moduleId",
                "()Ljava/lang/String;",
                null,
                null);
        moduleId.visitCode();
        moduleId.visitLdcInsn("gridelyx:smoke-module");
        moduleId.visitInsn(Opcodes.ARETURN);
        moduleId.visitMaxs(1, 1);
        moduleId.visitEnd();

        MethodVisitor prepare = writer.visitMethod(
                Opcodes.ACC_PUBLIC,
                "prepare",
                "(" + scope + ")V",
                null,
                null);
        prepare.visitCode();
        prepare.visitInsn(Opcodes.RETURN);
        prepare.visitMaxs(0, 2);
        prepare.visitEnd();

        MethodVisitor activate =
                writer.visitMethod(Opcodes.ACC_PUBLIC, "activate", "()V", null, null);
        activate.visitCode();
        activate.visitInsn(Opcodes.RETURN);
        activate.visitMaxs(0, 1);
        activate.visitEnd();

        writer.visitEnd();
        return writer.toByteArray();
    }

    private static void deleteTree(Path root) throws IOException {
        if (!Files.exists(root)) {
            return;
        }
        try (var paths = Files.walk(root)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.deleteIfExists(path);
            }
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }
}
