#pragma once

#include <cstddef>
#include <cstdint>
#include <span>
#include <string_view>

namespace gridelyx::bedrock {

enum class FrameType : std::uint32_t {
    control = 1,
    ual_operation = 2,
    mesh = 3,
    texture_patch = 4,
    world_delta = 5,
    telemetry = 6,
    script_result = 7,
};

struct FrameView {
    std::uint64_t sequence;
    FrameType type;
    std::span<const std::byte> payload;
};

class BedrockAdapter {
public:
    virtual ~BedrockAdapter() = default;
    virtual std::string_view name() const noexcept = 0;
    virtual bool submit(const FrameView& frame) = 0;
};

} // namespace gridelyx::bedrock
