cmake_minimum_required(VERSION 3.22)
project(madk_native LANGUAGES CXX)

add_library(madk_native SHARED src/madk_native.cpp)
target_include_directories(madk_native PUBLIC include)
target_compile_features(madk_native PUBLIC cxx_std_20)
set_target_properties(madk_native PROPERTIES CXX_EXTENSIONS OFF)
