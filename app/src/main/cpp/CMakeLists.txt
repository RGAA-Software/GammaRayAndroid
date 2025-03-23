cmake_minimum_required(VERSION 3.22.1)

include(env_settings.cmake)
set(CMAKE_TOOLCHAIN_FILE "${VCPKG_ROOT}/scripts/buildsystems/vcpkg.cmake")
set(VCPKG_CHAINLOAD_TOOLCHAIN_FILE "${CMAKE_ANDROID_NDK}/build/cmake/android.toolchain.cmake")
if(ANDROID_ABI STREQUAL "x86")
    set(VCPKG_TARGET_TRIPLET "x86-android")
elseif(ANDROID_ABI STREQUAL "x86_64")
    set(VCPKG_TARGET_TRIPLET "x64-android")
elseif(ANDROID_ABI STREQUAL "armeabi-v7a")
    set(VCPKG_TARGET_TRIPLET "arm-neon-android")
elseif(ANDROID_ABI STREQUAL "arm64-v8a")
    set(VCPKG_TARGET_TRIPLET "arm64-android")
endif()


string(REPLACE "\\" "/" CMAKE_ANDROID_NDK "${CMAKE_ANDROID_NDK}")
message("=== ===" ${CMAKE_ANDROID_NDK})

project("client")

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
if (ANDROID)
    add_compile_options(-Wno-error=deprecated-declarations)
endif ()
include_directories(${CMAKE_SOURCE_DIR})
include_directories(${CMAKE_SOURCE_DIR}/tc_3rdparty)
include_directories(${CMAKE_SOURCE_DIR}/tc_3rdparty/eventbus/src)
include_directories(${CMAKE_SOURCE_DIR}/tc_3rdparty/spdlog)
include_directories(${CMAKE_SOURCE_DIR}/tc_common_new)
include_directories(${CMAKE_SOURCE_DIR}/tc_client_sdk_new)
include_directories(${CMAKE_SOURCE_DIR}/tc_message_new)
include_directories(${CMAKE_SOURCE_DIR}/tc_relay_client)
include_directories(${CMAKE_SOURCE_DIR}/tc_3rdparty/opus/include)
include_directories(${CMAKE_SOURCE_DIR}/tc_3rdparty/asio2/include)
include_directories(${CMAKE_SOURCE_DIR}/tc_3rdparty/asio2/3rd)
include_directories(${CMAKE_SOURCE_DIR}/tc_3rdparty/asio2/3rd/asio)
include_directories(${VCPKG_ROOT}/installed/arm64-android/include)
include_directories(${CMAKE_BINARY_DIR}/tc_message_new)
message("===  ==> ${CMAKE_BINARY_DIR}/tc_message_new")

add_subdirectory(tc_3rdparty)
add_subdirectory(tc_common_new)
add_subdirectory(tc_client_sdk_new)
add_subdirectory(tc_message_new)
add_subdirectory(tc_opus_codec_new)
add_subdirectory(tc_relay_client)

find_package(oboe REQUIRED CONFIG)

add_library(${CMAKE_PROJECT_NAME} SHARED
        main.cpp
        application.cpp
        frame_render.cpp
        app_context.cpp
        audio_player.cpp
        native_msg_maker.cpp
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