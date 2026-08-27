#include "madk_native.h"

std::uint32_t madk_abi_version() {
    return 1U;
}

double madk_add(double left, double right) {
    return left + right;
}
