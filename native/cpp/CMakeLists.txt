add_library(gridelyx_native SHARED src/gridelyx_native.cpp)
target_include_directories(gridelyx_native PUBLIC include)
target_compile_features(gridelyx_native PUBLIC cxx_std_20)
set_target_properties(gridelyx_native PROPERTIES CXX_EXTENSIONS OFF)

if(UNIX AND NOT APPLE)
    target_link_libraries(gridelyx_native PRIVATE rt)
endif()
