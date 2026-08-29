package com.example.examplemod.advanced.validation;

// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/assets/DynamicModelRegistry.java
import com.example.examplemod.advanced.assets.DynamicModelRegistry;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/assets/DynamicTextureRegistry.java
import com.example.examplemod.advanced.assets.DynamicTextureRegistry;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/assets/MeshAsset.java
import com.example.examplemod.advanced.assets.MeshAsset;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/assets/TextureAsset.java
import com.example.examplemod.advanced.assets.TextureAsset;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/polyloader/ActivationStrategy.java
import com.example.examplemod.advanced.polyloader.ActivationStrategy;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/polyloader/AsmInvocationTranslator.java
import com.example.examplemod.advanced.polyloader.AsmInvocationTranslator;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/polyloader/CallTranslationRule.java
import com.example.examplemod.advanced.polyloader.CallTranslationRule;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/polyloader/ModArtifactAnalyzer.java
import com.example.examplemod.advanced.polyloader.ModArtifactAnalyzer;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/polyloader/ModArtifactProfile.java
import com.example.examplemod.advanced.polyloader.ModArtifactProfile;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/polyloader/SideloadMode.java
import com.example.examplemod.advanced.polyloader.SideloadMode;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/polyloader/UnifiedAbstractionLayer.java
import com.example.examplemod.advanced.polyloader.UnifiedAbstractionLayer;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/runtime/ClassHotSwapService.java
import com.example.examplemod.advanced.runtime.ClassHotSwapService;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/runtime/ExternalHotloadCore.java
import com.example.examplemod.advanced.runtime.ExternalHotloadCore;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/runtime/ReloadOrchestrator.java
import com.example.examplemod.advanced.runtime.ReloadOrchestrator;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/runtime/ReloadTargetBindings.java
import com.example.examplemod.advanced.runtime.ReloadTargetBindings;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/sandbox/PreparedWorldTransaction.java
import com.example.examplemod.advanced.sandbox.PreparedWorldTransaction;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/sandbox/ScriptSupervisor.java
import com.example.examplemod.advanced.sandbox.ScriptSupervisor;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/sandbox/TransactionalWorldSandbox.java
import com.example.examplemod.advanced.sandbox.TransactionalWorldSandbox;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/scripting/PolyglotScriptHost.java
import com.example.examplemod.advanced.scripting.PolyglotScriptHost;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/worldedit/SectionDelta.java
import com.example.examplemod.advanced.worldedit.SectionDelta;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/worldedit/SectionKey.java
import com.example.examplemod.advanced.worldedit.SectionKey;
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/templates/neoforge-26.2/src/advanced/java/com/example/examplemod/advanced/worldedit/WorldMutationSink.java
import com.example.examplemod.advanced.worldedit.WorldMutationSink;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public final class GridelyxSmokeTest {
    private GridelyxSmokeTest() {}

    public static void main(String[] args) throws Exception {
        run();
    }

    public static void run() throws Exception {
        testUnifiedAbstractionLayer();
        testAsmTranslation();
        testArtifactClassification();
        testDynamicAssets();
        testScriptDeadline();
        testTransactionalRollback();
        testReloadOrchestrator();
        System.out.println("PASS: Gridelyx runtime smoke test");
    }

    private static void testUnifiedAbstractionLayer() {
        UnifiedAbstractionLayer layer = new UnifiedAbstractionLayer();
        layer.register(
                UnifiedAbstractionLayer.Domain.REGISTRY,
                operation -> new UnifiedAbstractionLayer.OperationResult(
                        operation.action().equals("register"),
                        operation.action(),
                        operation.payload()));
        UnifiedAbstractionLayer.OperationResult result = layer.dispatch(
                UnifiedAbstractionLayer.Domain.REGISTRY,
                "register",
                com.example.examplemod.advanced.polyloader.LoaderFamily.FABRIC,
                Map.of("id", "gridelyx:test"),
                new byte[] {7});
        require(result.success(), "UAL dispatch did not reach the registered handler");
        require(result.payload()[0] == 7, "UAL payload changed during dispatch");
    }

    private static void testAsmTranslation() {
        byte[] source = classWithInvocation();
        CallTranslationRule rule = new CallTranslationRule(
                Opcodes.INVOKESTATIC,
                "source/Api",
                "ping",
                "()V",
                Opcodes.INVOKESTATIC,
                "target/Bridge",
                "ping",
                "()V",
                false);
        byte[] translated = new AsmInvocationTranslator().translate(source, java.util.List.of(rule));
        AtomicBoolean replacementFound = new AtomicBoolean();
        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9) {
            @Override
            public MethodVisitor visitMethod(
                    int access,
                    String name,
                    String descriptor,
                    String signature,
                    String[] exceptions) {
                return new MethodVisitor(Opcodes.ASM9) {
                    @Override
                    public void visitMethodInsn(
                            int opcode,
                            String owner,
                            String methodName,
                            String methodDescriptor,
                            boolean isInterface) {
                        if (owner.equals("target/Bridge") && methodName.equals("ping")) {
                            replacementFound.set(true);
                        }
                    }
                };
            }
        };
        new ClassReader(translated).accept(visitor, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        require(replacementFound.get(), "ASM invocation translation did not rewrite the target call");
    }

    private static byte[] classWithInvocation() {
        ClassWriter writer = new ClassWriter(0);
        writer.visit(Opcodes.V17, Opcodes.ACC_PUBLIC, "sample/TranslationFixture", null, "java/lang/Object", null);
        MethodVisitor method = writer.visitMethod(
                Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
                "run",
                "()V",
                null,
                null);
        method.visitCode();
        method.visitMethodInsn(Opcodes.INVOKESTATIC, "source/Api", "ping", "()V", false);
        method.visitInsn(Opcodes.RETURN);
        method.visitMaxs(0, 0);
        method.visitEnd();
        writer.visitEnd();
        return writer.toByteArray();
    }

    private static void testArtifactClassification() throws IOException {
        Path plain = Files.createTempFile("gridelyx-plain-", ".jar");
        Path fabric = Files.createTempFile("gridelyx-fabric-", ".jar");
        Path early = Files.createTempFile("gridelyx-early-", ".jar");
        try {
            createJar(plain);
            createJar(fabric, "fabric.mod.json");
            createJar(early, "fabric.mod.json", "example.mixins.json");
            ModArtifactAnalyzer analyzer = new ModArtifactAnalyzer();
            ModArtifactProfile plainProfile = analyzer.analyze(plain);
            ModArtifactProfile fabricProfile = analyzer.analyze(fabric);
            ModArtifactProfile earlyProfile = analyzer.analyze(early);
            require(plainProfile.recommendedMode() == SideloadMode.LIVE_SAFE, "plain JAR classification failed");
            require(fabricProfile.recommendedMode() == SideloadMode.EMULATED, "Fabric JAR classification failed");
            require(
                    earlyProfile.recommendedMode() == SideloadMode.PRELAUNCH_REQUIRED,
                    "Mixin-bearing JAR was not forced to prelaunch");
        } finally {
            Files.deleteIfExists(plain);
            Files.deleteIfExists(fabric);
            Files.deleteIfExists(early);
        }
    }

    private static void createJar(Path path, String... entries) throws IOException {
        try (OutputStream output = Files.newOutputStream(path);
                JarOutputStream jar = new JarOutputStream(output)) {
            for (String entryName : entries) {
                jar.putNextEntry(new JarEntry(entryName));
                jar.write("{}".getBytes(java.nio.charset.StandardCharsets.UTF_8));
                jar.closeEntry();
            }
        }
    }

    private static void testDynamicAssets() {
        DynamicModelRegistry models = new DynamicModelRegistry();
        MeshAsset mesh = models.publish(
                "gridelyx:test_mesh",
                3,
                new float[] {0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F},
                new int[] {0, 1, 2});
        require(mesh.vertexCount() == 3, "dynamic model registry returned the wrong vertex count");

        DynamicTextureRegistry textures = new DynamicTextureRegistry();
        TextureAsset initial = textures.publish("gridelyx:test_texture", 2, 2, new int[] {0, 0, 0, 0});
        TextureAsset patched = textures.patch("gridelyx:test_texture", 1, 0, 1, 2, new int[] {7, 9});
        require(patched.revision() > initial.revision(), "texture patch did not advance the revision");
        require(
                Arrays.equals(patched.rgbaPixels(), new int[] {0, 7, 0, 9}),
                "texture patch wrote unexpected pixels");
    }

    private static void testScriptDeadline() throws Exception {
        try (ScriptSupervisor supervisor = new ScriptSupervisor(1)) {
            ScriptSupervisor.ExecutionResult<Void> result = supervisor
                    .<Void>submit(
                            () -> {
                                while (!Thread.currentThread().isInterrupted()) {
                                    Thread.onSpinWait();
                                }
                                throw new InterruptedException("cancelled");
                            },
                            Duration.ofMillis(50))
                    .get(2, TimeUnit.SECONDS);
            require(result.status() == ScriptSupervisor.Status.TIMED_OUT, "script deadline did not win the race");
            require(result.interruptionRequested(), "script timeout did not request interruption");
        }
    }

    private static void testTransactionalRollback() {
        SectionKey first = new SectionKey(0, 0, 0);
        SectionKey second = new SectionKey(1, 0, 0);
        PreparedWorldTransaction transaction = new PreparedWorldTransaction(
                77L,
                java.util.List.of(
                        mutation(first, 5),
                        mutation(second, 6)));
        FakeSink sink = new FakeSink(second);
        TransactionalWorldSandbox.TransactionResult result =
                new TransactionalWorldSandbox().commit(sink, transaction, true);
        require(
                result.state() == TransactionalWorldSandbox.State.ROLLED_BACK,
                "world transaction did not report a complete rollback");
        require(sink.value(first) == 0 && sink.value(second) == 0, "rollback did not restore world values");
        require(sink.currentRevision(first) == 2L, "first section rollback revision is incorrect");
        require(sink.currentRevision(second) == 2L, "mutate-then-throw section was not rolled back");
        require(sink.reconciled(), "rollback did not request lighting reconciliation");
    }

    private static void testReloadOrchestrator() throws Exception {
        Path workspace = Files.createTempDirectory("gridelyx-reload-");
        Path classes = Files.createDirectories(workspace.resolve("classes"));
        Path dataFile = Files.createDirectories(workspace.resolve("data/gridelyx/test"))
                .resolve("recipes.json");
        Files.writeString(dataFile, "{}");
        AtomicInteger dataReloads = new AtomicInteger();
        AtomicInteger epochHandoffs = new AtomicInteger();

        ReloadTargetBindings bindings = ReloadTargetBindings.builder()
                .dataReload((path, change) -> dataReloads.incrementAndGet())
                .runtimeEpochHandoff((event, reason) -> epochHandoffs.incrementAndGet())
                .build();

        ExternalHotloadCore core = new ExternalHotloadCore();
        core.addRoot(workspace);
        try (ReloadOrchestrator orchestrator = new ReloadOrchestrator(
                core,
                new ClassHotSwapService(),
                new PolyglotScriptHost(),
                bindings,
                workspace,
                classes,
                GridelyxSmokeTest.class.getClassLoader())) {
            ReloadOrchestrator.ReloadResult dataResult = orchestrator.reloadNow(new ExternalHotloadCore.ReloadEvent(
                    dataFile,
                    ExternalHotloadCore.ReloadKind.DATA,
                    Instant.now(),
                    "ENTRY_MODIFY"));
            require(dataResult.status() == ReloadOrchestrator.Status.APPLIED, "data reload was not applied");
            require(
                    dataResult.strategy() == ActivationStrategy.TRANSACTIONAL_RELOAD,
                    "data reload selected the wrong activation strategy");
            require(dataReloads.get() == 1, "data target binding was not invoked exactly once");

            Path deletedClass = classes.resolve("example/Deleted.class");
            ReloadOrchestrator.ReloadResult classResult = orchestrator.reloadNow(new ExternalHotloadCore.ReloadEvent(
                    deletedClass,
                    ExternalHotloadCore.ReloadKind.JAVA_BYTECODE,
                    Instant.now(),
                    "ENTRY_DELETE"));
            require(classResult.status() == ReloadOrchestrator.Status.ESCALATED, "class deletion did not escalate");
            require(
                    classResult.strategy() == ActivationStrategy.RUNTIME_EPOCH_HANDOFF,
                    "structural class change did not select runtime epoch handoff");
            require(epochHandoffs.get() == 1, "runtime epoch target binding was not invoked exactly once");
        } finally {
            deleteTree(workspace);
        }
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

    private static PreparedWorldTransaction.MutationPair mutation(SectionKey key, int value) {
        SectionDelta forward = new SectionDelta(key, 0L, 1L, new int[] {0}, new int[] {value});
        SectionDelta rollback = new SectionDelta(key, 1L, 2L, new int[] {0}, new int[] {0});
        return new PreparedWorldTransaction.MutationPair(forward, rollback);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalStateException(message);
        }
    }

    private static final class FakeSink implements WorldMutationSink {
        private final Map<SectionKey, Long> revisions = new HashMap<>();
        private final Map<SectionKey, Integer> values = new HashMap<>();
        private final SectionKey mutateThenThrow;
        private boolean failureInjected;
        private boolean reconciled;

        private FakeSink(SectionKey mutateThenThrow) {
            this.mutateThenThrow = mutateThenThrow;
        }

        @Override
        public long currentRevision(SectionKey key) {
            return revisions.getOrDefault(key, 0L);
        }

        @Override
        public void applyWithoutLighting(SectionDelta delta) {
            if (currentRevision(delta.key()) != delta.baseRevision()) {
                throw new IllegalStateException("fake sink revision mismatch");
            }
            revisions.put(delta.key(), delta.newRevision());
            values.put(delta.key(), delta.paletteIndices()[0]);
            if (!failureInjected && delta.key().equals(mutateThenThrow) && delta.newRevision() == 1L) {
                failureInjected = true;
                throw new IllegalStateException("injected mutate-then-throw failure");
            }
        }

        @Override
        public void reconcileLighting(Set<SectionKey> dirtySections) {
            reconciled = !dirtySections.isEmpty();
        }

        @Override
        public void markForSave(Set<SectionKey> dirtySections) {}

        private int value(SectionKey key) {
            return values.getOrDefault(key, 0);
        }

        private boolean reconciled() {
            return reconciled;
        }
    }
}
