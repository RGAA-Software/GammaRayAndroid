cmake_minimum_required(VERSION 3.22.1)

include(env_settings.cmake)
set(CMAKE_TOOLCHAIN_FILE "${VCPKG_ROOT}/scripts/buildsystems/vcpkg.cmake")
set(VCPKG_CHAINLOAD_TOOLCHAIN_FILE "${CMAKE_ANDROID_NDK}/build/cmake/android.toolchain.cmake")
if(ANDROID_ABI STREQUAL "x86")
    set(VCPKG_TARGET_TRIPLET "x86-android")
elseif(ANDROID_ABI STREQUAL "x86_64")
    set(VCPKG_TARGET_TRIPLET "x64-android")
elseif(ANDROID_ABI STREQUAL "armeabi-v7a")
    set(VCPKG_TARGET_TRIPLET "arm-android")
elseif(ANDROID_ABI STREQUAL "arm64-v8a")
    set(VCPKG_TARGET_TRIPLET "arm64-android")
endif()


string(REPLACE "\\" "/" CMAKE_ANDROID_NDK "${CMAKE_ANDROID_NDK}")
message("======" ${CMAKE_ANDROID_NDK})

project("client")

include_directories(${CMAKE_SOURCE_DIR})
include_directories(${CMAKE_SOURCE_DIR}/tc_3rdparty)
include_directories(${CMAKE_SOURCE_DIR}/tc_3rdparty/eventbus/src)
include_directories(${CMAKE_SOURCE_DIR}/tc_3rdparty/spdlog)
include_directories(${CMAKE_SOURCE_DIR}/tc_common_new)
include_directories(${CMAKE_SOURCE_DIR}/tc_client_sdk_new)
include_directories(${CMAKE_SOURCE_DIR}/tc_message_new)

add_subdirectory(tc_3rdparty)
add_subdirectory(tc_common_new)
add_subdirectory(tc_client_sdk_new)
add_subdirectory(tc_message_new)
add_subdirectory(tc_opus_codec_new)

find_package(oboe REQUIRED CONFIG)

add_library(${CMAKE_PROJECT_NAME} SHARED
        main.cpp
        application.cpp
        frame_render.cpp
        app_context.cpp
        audio_player.cpp
)

target_link_libraries(${CMAKE_PROJECT_NAME}
        #GLESv2
        GLESv3
        EGL
        android
        log
        mediandk
        oboe::oboe
        tc_message
        tc_common_new
        tc_sdk)