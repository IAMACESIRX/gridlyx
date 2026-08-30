#pragma once

#include <cstdint>

#if defined(_WIN32)
#define GRIDELYX_EXPORT __declspec(dllexport)
#else
#define GRIDELYX_EXPORT __attribute__((visibility("default")))
#endif

extern "C" {

struct gridelyx_shm_handle;

struct gridelyx_frame_snapshot {
    std::uint64_t sequence;
    std::uint32_t type;
    std::uint32_t length;
    std::uint32_t crc32;
    std::uint32_t reserved;
};

GRIDELYX_EXPORT std::uint32_t gridelyx_abi_version();
GRIDELYX_EXPORT std::uint32_t gridelyx_protocol_version();
GRIDELYX_EXPORT double gridelyx_add(double left, double right);

GRIDELYX_EXPORT gridelyx_shm_handle* gridelyx_shm_create(
    const char* name,
    std::uint32_t payload_capacity);
GRIDELYX_EXPORT gridelyx_shm_handle* gridelyx_shm_open(const char* name);
GRIDELYX_EXPORT std::uint8_t* gridelyx_shm_payload(gridelyx_shm_handle* handle);
GRIDELYX_EXPORT std::uint32_t gridelyx_shm_capacity(const gridelyx_shm_handle* handle);
GRIDELYX_EXPORT std::uint64_t gridelyx_shm_sequence(const gridelyx_shm_handle* handle);
GRIDELYX_EXPORT int gridelyx_shm_publish(
    gridelyx_shm_handle* handle,
    std::uint32_t type,
    std::uint32_t length,
    std::uint32_t crc32);
GRIDELYX_EXPORT int gridelyx_shm_snapshot(
    const gridelyx_shm_handle* handle,
    gridelyx_frame_snapshot* snapshot);
GRIDELYX_EXPORT void gridelyx_shm_close(gridelyx_shm_handle* handle);
GRIDELYX_EXPORT int gridelyx_shm_unlink(const char* name);

}
