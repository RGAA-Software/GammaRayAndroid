### How to Build
### Install dependences
#### 1. Change to VCPKG folder 
```c++
    cd {your-project-folder}/app/src/main/cpp/tc_3rdparty/vcpkg
```

#### 2. Install vcpkg.exe
```c++
    ./bootstrap-vcpkg.bat
```

#### 3. Install dependences
```c++
    ./vcpkg.exe install sqlite3:arm64-android
    ./vcpkg.exe install ffmpeg:arm64-android
    ./vcpkg.exe install protobuf:arm64-android
    ./vcpkg.exe install openssl:arm64-android
    ./vcpkg.exe install libyuv:arm64-android
    ./vcpkg.exe install glm:arm64-android
```