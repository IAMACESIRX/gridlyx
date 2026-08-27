package com.example.examplemod.advanced.ai;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class McpEndpoint {
    public static final String PROTOCOL_VERSION = "2026-07-28";

    private final ConcurrentHashMap<String, ToolDefinition> tools = new ConcurrentHashMap<>();

    public void registerTool(String name, String description, Tool tool) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("MCP tool name is required");
        }
        tools.put(name, new ToolDefinition(name, description, tool));
    }

    public Response handle(Request request) {
        if (!PROTOCOL_VERSION.equals(request.protocolVersion())) {
            return Response.error("Unsupported MCP protocol revision: " + request.protocolVersion());
        }
        try {
            return switch (request.method()) {
                case "server/discover" -> Response.ok(discover());
                case "tools/list" -> Response.ok(listTools());
                case "tools/call" -> callTool(request);
                default -> Response.error("Unsupported MCP method: " + request.method());
            };
        } catch (Exception exception) {
            return Response.error(exception.getClass().getSimpleName() + ": " + exception.getMessage());
        }
    }

    private Map<String, Object> discover() {
        Map<String, Object> capabilities = new LinkedHashMap<>();
        capabilities.put("protocolVersion", PROTOCOL_VERSION);
        capabilities.put("stateless", true);
        capabilities.put("tools", true);
        capabilities.put("resources", true);
        capabilities.put("extensions", List.of("io.iamacesirx.madk/local-vector-index"));
        return capabilities;
    }

    private List<Map<String, String>> listTools() {
        List<ToolDefinition> definitions = new ArrayList<>(tools.values());
        definitions.sort(Comparator.comparing(ToolDefinition::name));
        List<Map<String, String>> result = new ArrayList<>(definitions.size());
        for (ToolDefinition definition : definitions) {
            result.add(Map.of(
                    "name", definition.name(),
                    "description", definition.description()));
        }
        return List.copyOf(result);
    }

    private Response callTool(Request request) throws Exception {
        ToolDefinition definition = tools.get(request.name());
        if (definition == null) {
            return Response.error("Unknown MCP tool: " + request.name());
        }
        Map<String, Object> arguments = request.arguments() == null ? Map.of() : request.arguments();
        return Response.ok(definition.tool().call(arguments));
    }

    @FunctionalInterface
    public interface Tool {
        Object call(Map<String, Object> arguments) throws Exception;
    }

    private record ToolDefinition(String name, String description, Tool tool) {}

    public record Request(
            String protocolVersion,
            String method,
            String name,
            Map<String, Object> arguments) {}

    public record Response(boolean ok, Object result, String error) {
        public static Response ok(Object result) {
            return new Response(true, result, null);
        }

        public static Response error(String error) {
            return new Response(false, null, error);
        }
    }
}
