package com.example.examplemod.advanced.worldedit;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.zip.GZIPInputStream;

public final class NbtStructureBlueprintLoader {
    private static final int MAX_COLLECTION_LENGTH = 16_777_216;

    public StructureBlueprint load(InputStream input) throws IOException {
        try (DataInputStream data = new DataInputStream(openMaybeCompressed(input))) {
            int rootType = data.readUnsignedByte();
            if (rootType != 10) {
                throw new IOException("Structure NBT root must be a compound tag");
            }
            data.readUTF();
            Map<String, Object> root = readCompound(data);
            return decodeStructure(root);
        }
    }

    private static InputStream openMaybeCompressed(InputStream input) throws IOException {
        BufferedInputStream buffered = new BufferedInputStream(input);
        buffered.mark(2);
        int first = buffered.read();
        int second = buffered.read();
        buffered.reset();
        if (first == 0x1f && second == 0x8b) {
            return new GZIPInputStream(buffered);
        }
        return buffered;
    }

    private static StructureBlueprint decodeStructure(Map<String, Object> root) throws IOException {
        List<?> size = requireList(root, "size");
        if (size.size() != 3) {
            throw new IOException("Structure size must contain three integers");
        }
        int sizeX = requireInt(size.get(0));
        int sizeY = requireInt(size.get(1));
        int sizeZ = requireInt(size.get(2));

        List<String> palette = new ArrayList<>();
        for (Object entry : requireList(root, "palette")) {
            palette.add(canonicalBlockState(requireMap(entry)));
        }
        int airIndex = palette.indexOf("minecraft:air");
        if (airIndex < 0) {
            airIndex = palette.size();
            palette.add("minecraft:air");
        }

        long volume = (long) sizeX * sizeY * sizeZ;
        if (volume <= 0 || volume > Integer.MAX_VALUE) {
            throw new IOException("Structure volume is invalid or too large");
        }
        int[] voxels = new int[(int) volume];
        Arrays.fill(voxels, airIndex);
        Map<Integer, Map<String, Object>> metadata = new LinkedHashMap<>();
        for (Object blockEntry : requireList(root, "blocks")) {
            Map<String, Object> block = requireMap(blockEntry);
            List<?> position = requireList(block, "pos");
            if (position.size() != 3) {
                throw new IOException("Block position must contain three integers");
            }
            int x = requireInt(position.get(0));
            int y = requireInt(position.get(1));
            int z = requireInt(position.get(2));
            if (x < 0 || y < 0 || z < 0 || x >= sizeX || y >= sizeY || z >= sizeZ) {
                throw new IOException("Block position outside structure bounds");
            }
            int state = requireInt(block.get("state"));
            if (state < 0 || state >= palette.size()) {
                throw new IOException("Palette index outside structure palette");
            }
            int index = (y * sizeZ + z) * sizeX + x;
            voxels[index] = state;
            Object nbt = block.get("nbt");
            if (nbt instanceof Map<?, ?>) {
                metadata.put(index, requireMap(nbt));
            }
        }
        return new StructureBlueprint(sizeX, sizeY, sizeZ, palette, voxels, metadata);
    }

    private static String canonicalBlockState(Map<String, Object> entry) throws IOException {
        Object nameValue = entry.get("Name");
        if (!(nameValue instanceof String name) || name.isBlank()) {
            throw new IOException("Palette entry is missing Name");
        }
        Object propertiesValue = entry.get("Properties");
        if (!(propertiesValue instanceof Map<?, ?> propertiesRaw) || propertiesRaw.isEmpty()) {
            return name;
        }
        Map<String, String> properties = new TreeMap<>();
        for (Map.Entry<?, ?> property : propertiesRaw.entrySet()) {
            properties.put(String.valueOf(property.getKey()), String.valueOf(property.getValue()));
        }
        StringBuilder builder = new StringBuilder(name).append('[');
        boolean first = true;
        for (Map.Entry<String, String> property : properties.entrySet()) {
            if (!first) {
                builder.append(',');
            }
            first = false;
            builder.append(property.getKey()).append('=').append(property.getValue());
        }
        return builder.append(']').toString();
    }

    private static Map<String, Object> readCompound(DataInputStream data) throws IOException {
        Map<String, Object> result = new LinkedHashMap<>();
        while (true) {
            int type;
            try {
                type = data.readUnsignedByte();
            } catch (EOFException exception) {
                throw new IOException("Unexpected end of NBT compound", exception);
            }
            if (type == 0) {
                return result;
            }
            String name = data.readUTF();
            result.put(name, readPayload(data, type));
        }
    }

    private static Object readPayload(DataInputStream data, int type) throws IOException {
        return switch (type) {
            case 1 -> data.readByte();
            case 2 -> data.readShort();
            case 3 -> data.readInt();
            case 4 -> data.readLong();
            case 5 -> data.readFloat();
            case 6 -> data.readDouble();
            case 7 -> readByteArray(data);
            case 8 -> data.readUTF();
            case 9 -> readList(data);
            case 10 -> readCompound(data);
            case 11 -> readIntArray(data);
            case 12 -> readLongArray(data);
            default -> throw new IOException("Unsupported NBT tag type: " + type);
        };
    }

    private static List<Object> readList(DataInputStream data) throws IOException {
        int elementType = data.readUnsignedByte();
        int length = checkedLength(data.readInt());
        List<Object> values = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            values.add(readPayload(data, elementType));
        }
        return values;
    }

    private static byte[] readByteArray(DataInputStream data) throws IOException {
        int length = checkedLength(data.readInt());
        return data.readNBytes(length);
    }

    private static int[] readIntArray(DataInputStream data) throws IOException {
        int length = checkedLength(data.readInt());
        int[] values = new int[length];
        for (int i = 0; i < length; i++) {
            values[i] = data.readInt();
        }
        return values;
    }

    private static long[] readLongArray(DataInputStream data) throws IOException {
        int length = checkedLength(data.readInt());
        long[] values = new long[length];
        for (int i = 0; i < length; i++) {
            values[i] = data.readLong();
        }
        return values;
    }

    private static int checkedLength(int length) throws IOException {
        if (length < 0 || length > MAX_COLLECTION_LENGTH) {
            throw new IOException("NBT collection length outside safety limit: " + length);
        }
        return length;
    }

    private static List<?> requireList(Map<String, Object> map, String key) throws IOException {
        Object value = map.get(key);
        if (!(value instanceof List<?> list)) {
            throw new IOException("NBT field is not a list: " + key);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> requireMap(Object value) throws IOException {
        if (!(value instanceof Map<?, ?> map)) {
            throw new IOException("NBT value is not a compound");
        }
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!(entry.getKey() instanceof String)) {
                throw new IOException("NBT compound key is not a string");
            }
        }
        return (Map<String, Object>) map;
    }

    private static int requireInt(Object value) throws IOException {
        if (!(value instanceof Number number)) {
            throw new IOException("NBT numeric value is missing");
        }
        return number.intValue();
    }
}
