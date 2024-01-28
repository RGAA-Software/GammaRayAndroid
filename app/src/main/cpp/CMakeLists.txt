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

#include(${VCPKG_ROOT}/installed/arm64-android/share/leveldb/leveldbConfig.cmake)
#message("======" ${VCPKG_ROOT}/installed/arm64-android/share/leveldb/leveldbConfig.cmake)
#include(${VCPKG_ROOT}/scripts/buildsystems/vcpkg.cmake)
#link_directories(${VCPKG_ROOT}/installed/arm64-android/lib)
#include_directories(${VCPKG_ROOT}/installed/arm64-android/include)

add_subdirectory(tc_3rdparty)
add_subdirectory(tc_common)
add_subdirectory(tc_client_sdk)
add_subdirectory(tc_message)

include_directories(${CMAKE_SOURCE_DIR})
include_directories(${CMAKE_SOURCE_DIR}/tc_3rdparty)
include_directories(${CMAKE_SOURCE_DIR}/tc_3rdparty/eventbus/src)
include_directories(${CMAKE_SOURCE_DIR}/tc_common)
include_directories(${CMAKE_SOURCE_DIR}/tc_client_sdk)
include_directories(${CMAKE_SOURCE_DIR}/tc_message)

add_library(${CMAKE_PROJECT_NAME} SHARED
        main.cpp
        application.cpp
        frame_render.cpp
        app_context.cpp
)

target_link_libraries(${CMAKE_PROJECT_NAME}
        #GLESv2
        GLESv3
        EGL
        android
        log
        mediandk
        tc_message
        tc_common
        tc_sdk)