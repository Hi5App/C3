project(yolov8ncnn)

cmake_minimum_required(VERSION 3.10)
#cmake_minimum_required(VERSION 3.4.1)

set(OpenCV_DIR ${CMAKE_SOURCE_DIR}/OpenCV-android-sdk/sdk/native/jni)
find_package(OpenCV REQUIRED core imgproc)

set(ncnn_DIR ${CMAKE_SOURCE_DIR}/${ANDROID_ABI}/lib/cmake/ncnn)
find_package(ncnn REQUIRED)

add_library(yolov8ncnn SHARED yolov8ncnn.cpp yolo.cpp ndkcamera.cpp)

target_link_libraries(yolov8ncnn ncnn ${OpenCV_LIBS} camera2ndk mediandk)
