package com.example.examplemod.advanced.clientdev;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

public final class DirectJavaCompiler {
    public CompilationResult compile(String className, String source) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            return new CompilationResult(false, Map.of(), List.of("JDK compiler is unavailable"));
        }
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager standard = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8);
                MemoryFileManager files = new MemoryFileManager(standard)) {
            JavaFileObject sourceFile = new SourceFile(className, source);
            JavaCompiler.CompilationTask task = compiler.getTask(
                    null,
                    files,
                    diagnostics,
                    List.of("--release", "25", "-Xlint:all"),
                    null,
                    List.of(sourceFile));
            boolean success = Boolean.TRUE.equals(task.call());
            return new CompilationResult(success, files.classBytes(), formatDiagnostics(diagnostics));
        } catch (IOException exception) {
            return new CompilationResult(false, Map.of(), List.of(exception.toString()));
        }
    }

    private static List<String> formatDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics) {
        return diagnostics.getDiagnostics().stream()
                .map(diagnostic -> diagnostic.getKind() + " line " + diagnostic.getLineNumber() + ": "
                        + diagnostic.getMessage(null))
                .toList();
    }

    public record CompilationResult(boolean success, Map<String, byte[]> classes, List<String> diagnostics) {
        public CompilationResult {
            classes = Map.copyOf(classes);
            diagnostics = List.copyOf(diagnostics);
        }
    }

    private static final class SourceFile extends SimpleJavaFileObject {
        private final String source;

        private SourceFile(String className, String source) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.source = source;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return source;
        }
    }

    private static final class ClassFile extends SimpleJavaFileObject {
        private final ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        private ClassFile(String className) {
            super(URI.create("bytes:///" + className.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
        }

        @Override
        public ByteArrayOutputStream openOutputStream() {
            return bytes;
        }
    }

    private static final class MemoryFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private final Map<String, ClassFile> output = new LinkedHashMap<>();

        private MemoryFileManager(StandardJavaFileManager fileManager) {
            super(fileManager);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(
                JavaFileManager.Location location,
                String className,
                JavaFileObject.Kind kind,
                FileObject sibling) {
            ClassFile file = new ClassFile(className);
            output.put(className, file);
            return file;
        }

        private Map<String, byte[]> classBytes() {
            Map<String, byte[]> result = new LinkedHashMap<>();
            for (Map.Entry<String, ClassFile> entry : output.entrySet()) {
                result.put(entry.getKey(), entry.getValue().bytes.toByteArray());
            }
            return result;
        }
    }
}
