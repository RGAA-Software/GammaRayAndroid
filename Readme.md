### GammaRay client for Android
#### !!! Please visit the repos !!!
**[Server](https://github.com/RGAA-Software/GammaRay)**  
**[PC Client](https://github.com/RGAA-Software/GammaRayPC)**

#### 2 Run a Android Client
> Download and install the GammaRay_Official_xxx.apk first
##### 2.1 Scan the QR in server panel, it will connect automatically.
![](docs/images/android_1.png)

##### 2.2 Change to 2nd tab in the bottom, you'll see your games and 2 fixed options(Desktop, Steam Big Picture). Toch the Desktop(the first item).
![](docs/images/android_2.png)

##### 2.3 If it connected to the server, you'll see the frame that same as your server.
![](docs/images/android_3.png)

##### 2.4 If you toch the Steam Big Picture(the second item), your Server will run the steam in Big Piture Mode. You can turn Virtual Joystick on in settings tab(the 4th tab).
![](docs/images/android_4.png)

##### 2.5 If you just want to listen to the music that playing on your Server, you can switch to the 3rd tab, there are some buildin music spectrum effects, examples:
![](docs/images/android_5.jpg)
![](docs/images/android_7.jpg)
![](docs/images/android_8.jpg)
![](docs/images/android_9.jpg)

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