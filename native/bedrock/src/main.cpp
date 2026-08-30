#include "gridelyx_bedrock_adapter.h"
// Gridelyx local reference: https://github.com/IAMACESIRX/gridlyx/blob/main/native/cpp/include/gridelyx_native.h
#include "gridelyx_native.h"

#include <chrono>
#include <cstddef>
#include <cstdint>
#include <cstring>
#include <iostream>
#include <string>
#include <thread>
#include <vector>

namespace {
std::uint32_t crc32(std::span<const std::byte> bytes) {
    std::uint32_t crc = 0xFFFFFFFFU;
    for (std::byte value : bytes) {
        crc ^= static_cast<std::uint8_t>(value);
        for (int bit = 0; bit < 8; ++bit) {
            const std::uint32_t mask = 0U - (crc & 1U);
            crc = (crc >> 1U) ^ (0xEDB88320U & mask);
        }
    }
    return ~crc;
}

class LoggingAdapter final : public gridelyx::bedrock::BedrockAdapter {
public:
    std::string_view name() const noexcept override {
        return "logging-adapter";
    }

    bool submit(const gridelyx::bedrock::FrameView& frame) override {
        std::cout << "Gridelyx Bedrock frame seq=" << frame.sequence
                  << " type=" << static_cast<std::uint32_t>(frame.type)
                  << " bytes=" << frame.payload.size() << '\n';
        return true;
    }
};
}

int main(int argc, char** argv) {
    const std::string shared_name = argc > 1 ? argv[1] : "gridelyx-bedrock";
    const bool once = argc > 2 && std::strcmp(argv[2], "--once") == 0;

    gridelyx_shm_handle* shared = gridelyx_shm_open(shared_name.c_str());
    if (shared == nullptr) {
        std::cerr << "Unable to open Gridelyx shared memory: " << shared_name << '\n';
        return 2;
    }

    LoggingAdapter adapter;
    std::uint64_t consumed = 0U;
    int exit_code = 0;

    do {
        gridelyx_frame_snapshot snapshot {};
        const int status = gridelyx_shm_snapshot(shared, &snapshot);
        if (status == 1 && snapshot.sequence != consumed) {
            const std::uint32_t capacity = gridelyx_shm_capacity(shared);
            if (snapshot.length > capacity) {
                std::cerr << "Gridelyx frame length exceeds shared-memory capacity\n";
                exit_code = 3;
                break;
            }

            const std::uint8_t* source = gridelyx_shm_payload(shared);
            std::vector<std::byte> payload(snapshot.length);
            std::memcpy(payload.data(), source, snapshot.length);
            if (gridelyx_shm_sequence(shared) != snapshot.sequence) {
                continue;
            }
            if (crc32(payload) != snapshot.crc32) {
                std::cerr << "Gridelyx frame CRC mismatch; dropping revision " << snapshot.sequence << '\n';
                consumed = snapshot.sequence;
                continue;
            }

            const gridelyx::bedrock::FrameView frame {
                snapshot.sequence,
                static_cast<gridelyx::bedrock::FrameType>(snapshot.type),
                payload,
            };
            if (!adapter.submit(frame)) {
                std::cerr << "Bedrock adapter rejected Gridelyx frame\n";
                exit_code = 4;
                break;
            }
            consumed = snapshot.sequence;
        }

        if (!once) {
            std::this_thread::sleep_for(std::chrono::milliseconds(1));
        }
    } while (!once);

    gridelyx_shm_close(shared);
    return exit_code;
}
