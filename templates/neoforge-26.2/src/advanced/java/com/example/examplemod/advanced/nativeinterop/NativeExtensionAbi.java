package com.example.examplemod.advanced.nativeinterop;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;

public final class NativeExtensionAbi implements AutoCloseable {
    private final Arena arena = Arena.ofShared();
    private final Linker linker = Linker.nativeLinker();
    private final SymbolLookup symbols;

    public NativeExtensionAbi(Path library) {
        symbols = SymbolLookup.libraryLookup(library, arena);
    }

    public MethodHandle bind(String symbol, FunctionDescriptor descriptor) {
        return linker.downcallHandle(
                symbols.find(symbol).orElseThrow(() -> new IllegalArgumentException(
                        "Native symbol not found: " + symbol)),
                descriptor);
    }

    @Override
    public void close() {
        arena.close();
    }
}
