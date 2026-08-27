#include "gridelyx_native.h"

#include <cstddef>
#include <cstdint>
#include <cstring>
#include <new>
#include <string>

#if defined(_WIN32)
#define NOMINMAX
#include <Windows.h>
#else
#include <fcntl.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <unistd.h>
#endif

namespace {
constexpr std::uint32_t kMagic = 0x5646534DU; // VFSM
constexpr std::uint32_t kProtocolVersion = 1U;

struct alignas(8) SharedHeader {
    std::uint32_t magic;
    std::uint32_t protocol_version;
    std::uint32_t payload_capacity;
    std::uint32_t reserved0;
    alignas(8) std::uint64_t sequence;
    std::uint32_t frame_type;
    std::uint32_t payload_length;
    std::uint32_t crc32;
    std::uint32_t reserved1;
};

static_assert(offsetof(SharedHeader, sequence) % alignof(std::uint64_t) == 0);

std::string normalise_name(const char* name) {
    if (name == nullptr || *name == '\0') {
        return {};
    }
#if defined(_WIN32)
    return std::string(name);
#else
    std::string value(name);
    for (char& character : value) {
        if (character == '/') {
            character = '_';
        }
    }
    return "/" + value;
#endif
}

std::uint64_t load_sequence(const std::uint64_t* value) {
#if defined(_WIN32)
    auto* atomic_value = reinterpret_cast<volatile LONG64*>(const_cast<std::uint64_t*>(value));
    return static_cast<std::uint64_t>(InterlockedCompareExchange64(atomic_value, 0, 0));
#else
    return __atomic_load_n(value, __ATOMIC_ACQUIRE);
#endif
}

std::uint64_t increment_sequence(std::uint64_t* value) {
#if defined(_WIN32)
    auto* atomic_value = reinterpret_cast<volatile LONG64*>(value);
    return static_cast<std::uint64_t>(InterlockedIncrement64(atomic_value));
#else
    return __atomic_add_fetch(value, 1U, __ATOMIC_RELEASE);
#endif
}
} // namespace

struct gridelyx_shm_handle {
    void* base = nullptr;
    std::size_t mapped_bytes = 0;
    std::string name;
#if defined(_WIN32)
    HANDLE mapping = nullptr;
#else
    int descriptor = -1;
#endif
};

namespace {
SharedHeader* header(gridelyx_shm_handle* handle) {
    return handle == nullptr ? nullptr : static_cast<SharedHeader*>(handle->base);
}

const SharedHeader* header(const gridelyx_shm_handle* handle) {
    return handle == nullptr ? nullptr : static_cast<const SharedHeader*>(handle->base);
}

bool valid_header(const SharedHeader* value) {
    return value != nullptr && value->magic == kMagic && value->protocol_version == kProtocolVersion;
}

void destroy_handle(gridelyx_shm_handle* handle) {
    if (handle == nullptr) {
        return;
    }
#if defined(_WIN32)
    if (handle->base != nullptr) {
        UnmapViewOfFile(handle->base);
    }
    if (handle->mapping != nullptr) {
        CloseHandle(handle->mapping);
    }
#else
    if (handle->base != nullptr && handle->mapped_bytes > 0) {
        munmap(handle->base, handle->mapped_bytes);
    }
    if (handle->descriptor >= 0) {
        close(handle->descriptor);
    }
#endif
    delete handle;
}
} // namespace

std::uint32_t gridelyx_abi_version() {
    return 1U;
}

std::uint32_t gridelyx_protocol_version() {
    return kProtocolVersion;
}

double gridelyx_add(double left, double right) {
    return left + right;
}

gridelyx_shm_handle* gridelyx_shm_create(const char* name, std::uint32_t payload_capacity) {
    if (payload_capacity == 0U) {
        return nullptr;
    }
    const std::string shared_name = normalise_name(name);
    if (shared_name.empty()) {
        return nullptr;
    }

    const std::size_t bytes = sizeof(SharedHeader) + static_cast<std::size_t>(payload_capacity);
    auto* handle = new (std::nothrow) gridelyx_shm_handle();
    if (handle == nullptr) {
        return nullptr;
    }
    handle->name = shared_name;
    handle->mapped_bytes = bytes;

#if defined(_WIN32)
    handle->mapping = CreateFileMappingA(
        INVALID_HANDLE_VALUE,
        nullptr,
        PAGE_READWRITE,
        0,
        static_cast<DWORD>(bytes),
        shared_name.c_str());
    if (handle->mapping == nullptr) {
        destroy_handle(handle);
        return nullptr;
    }
    handle->base = MapViewOfFile(handle->mapping, FILE_MAP_ALL_ACCESS, 0, 0, bytes);
#else
    handle->descriptor = shm_open(shared_name.c_str(), O_CREAT | O_RDWR, 0600);
    if (handle->descriptor < 0 || ftruncate(handle->descriptor, static_cast<off_t>(bytes)) != 0) {
        destroy_handle(handle);
        return nullptr;
    }
    handle->base = mmap(nullptr, bytes, PROT_READ | PROT_WRITE, MAP_SHARED, handle->descriptor, 0);
    if (handle->base == MAP_FAILED) {
        handle->base = nullptr;
    }
#endif

    if (handle->base == nullptr) {
        destroy_handle(handle);
        return nullptr;
    }

    std::memset(handle->base, 0, bytes);
    SharedHeader* shared = header(handle);
    shared->magic = kMagic;
    shared->protocol_version = kProtocolVersion;
    shared->payload_capacity = payload_capacity;
    return handle;
}

gridelyx_shm_handle* gridelyx_shm_open(const char* name) {
    const std::string shared_name = normalise_name(name);
    if (shared_name.empty()) {
        return nullptr;
    }

    auto* handle = new (std::nothrow) gridelyx_shm_handle();
    if (handle == nullptr) {
        return nullptr;
    }
    handle->name = shared_name;

#if defined(_WIN32)
    handle->mapping = OpenFileMappingA(FILE_MAP_ALL_ACCESS, FALSE, shared_name.c_str());
    if (handle->mapping == nullptr) {
        destroy_handle(handle);
        return nullptr;
    }
    handle->base = MapViewOfFile(handle->mapping, FILE_MAP_ALL_ACCESS, 0, 0, 0);
    if (handle->base == nullptr || !valid_header(header(handle))) {
        destroy_handle(handle);
        return nullptr;
    }
    handle->mapped_bytes = sizeof(SharedHeader) + header(handle)->payload_capacity;
#else
    handle->descriptor = shm_open(shared_name.c_str(), O_RDWR, 0600);
    if (handle->descriptor < 0) {
        destroy_handle(handle);
        return nullptr;
    }
    struct stat status {};
    if (fstat(handle->descriptor, &status) != 0 || status.st_size < static_cast<off_t>(sizeof(SharedHeader))) {
        destroy_handle(handle);
        return nullptr;
    }
    handle->mapped_bytes = static_cast<std::size_t>(status.st_size);
    handle->base = mmap(nullptr, handle->mapped_bytes, PROT_READ | PROT_WRITE, MAP_SHARED, handle->descriptor, 0);
    if (handle->base == MAP_FAILED) {
        handle->base = nullptr;
    }
    if (handle->base == nullptr || !valid_header(header(handle))) {
        destroy_handle(handle);
        return nullptr;
    }
#endif

    return handle;
}

std::uint8_t* gridelyx_shm_payload(gridelyx_shm_handle* handle) {
    SharedHeader* shared = header(handle);
    if (!valid_header(shared)) {
        return nullptr;
    }
    auto* bytes = static_cast<std::uint8_t*>(handle->base);
    return bytes + sizeof(SharedHeader);
}

std::uint32_t gridelyx_shm_capacity(const gridelyx_shm_handle* handle) {
    const SharedHeader* shared = header(handle);
    return valid_header(shared) ? shared->payload_capacity : 0U;
}

std::uint64_t gridelyx_shm_sequence(const gridelyx_shm_handle* handle) {
    const SharedHeader* shared = header(handle);
    return valid_header(shared) ? load_sequence(&shared->sequence) : 0U;
}

int gridelyx_shm_publish(
    gridelyx_shm_handle* handle,
    std::uint32_t type,
    std::uint32_t length,
    std::uint32_t crc32) {
    SharedHeader* shared = header(handle);
    if (!valid_header(shared) || length > shared->payload_capacity) {
        return 0;
    }
    shared->frame_type = type;
    shared->payload_length = length;
    shared->crc32 = crc32;
    increment_sequence(&shared->sequence);
    return 1;
}

int gridelyx_shm_snapshot(
    const gridelyx_shm_handle* handle,
    gridelyx_frame_snapshot* snapshot) {
    const SharedHeader* shared = header(handle);
    if (!valid_header(shared) || snapshot == nullptr) {
        return -1;
    }
    const std::uint64_t before = load_sequence(&shared->sequence);
    if (before == 0U) {
        return 0;
    }
    gridelyx_frame_snapshot candidate {
        before,
        shared->frame_type,
        shared->payload_length,
        shared->crc32,
        0U,
    };
    const std::uint64_t after = load_sequence(&shared->sequence);
    if (before != after) {
        return -2;
    }
    *snapshot = candidate;
    return 1;
}

void gridelyx_shm_close(gridelyx_shm_handle* handle) {
    destroy_handle(handle);
}

int gridelyx_shm_unlink(const char* name) {
    const std::string shared_name = normalise_name(name);
    if (shared_name.empty()) {
        return 0;
    }
#if defined(_WIN32)
    return 1;
#else
    return shm_unlink(shared_name.c_str()) == 0 ? 1 : 0;
#endif
}
