package com.example.examplemod.advanced.render;

import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL45C;

public final class NativeGpuBuffer implements AutoCloseable {
    private int bufferId;

    public void upload(ByteBuffer data, int usage) {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }
        requireContext();
        if (bufferId == 0) {
            bufferId = GL45C.glCreateBuffers();
        }
        GL45C.glNamedBufferData(bufferId, data, usage);
    }

    public int id() {
        return bufferId;
    }

    private static void requireContext() {
        try {
            GL.getCapabilities();
        } catch (IllegalStateException exception) {
            throw new IllegalStateException("OpenGL context/capabilities are not active on this thread", exception);
        }
    }

    @Override
    public void close() {
        if (bufferId != 0) {
            requireContext();
            GL45C.glDeleteBuffers(bufferId);
            bufferId = 0;
        }
    }
}
