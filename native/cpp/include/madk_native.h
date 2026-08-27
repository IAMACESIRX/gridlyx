#pragma once

#include <cstdint>

#if defined(_WIN32)
#define MADK_EXPORT __declspec(dllexport)
#else
#define MADK_EXPORT __attribute__((visibility("default")))
#endif

extern "C" {
MADK_EXPORT std::uint32_t madk_abi_version();
MADK_EXPORT double madk_add(double left, double right);
}
