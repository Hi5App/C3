// Tencent is pleased to support the open source community by making ncnn available.
//
// Copyright (C) 2021 THL A29 Limited, a Tencent company. All rights reserved.
//
// Licensed under the BSD 3-Clause License (the "License"); you may not use this file except
// in compliance with the License. You may obtain a copy of the License at
//
// https://opensource.org/licenses/BSD-3-Clause
//
// Unless required by applicable law or agreed to in writing, software distributed
// under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
// CONDITIONS OF ANY KIND, either express or implied. See the License for the
// specific language governing permissions and limitations under the License.

#include <android/asset_manager_jni.h>
#include <android/native_window_jni.h>
#include <android/native_window.h>

#include <android/log.h>
#define LOG_TAG "YoloV8NcnnNative"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

#include <jni.h>

#include <string>
#include <vector>

#include <platform.h>
#include <benchmark.h>

#include "yolo.h"

#include "ndkcamera.h"

#include <opencv2/core/core.hpp>
#include <opencv2/imgproc/imgproc.hpp>

#if __ARM_NEON
#include <arm_neon.h>
#endif // __ARM_NEON

static int draw_unsupported(cv::Mat& rgb)
{
    const char text[] = "unsupported";

    int baseLine = 0;
    cv::Size label_size = cv::getTextSize(text, cv::FONT_HERSHEY_SIMPLEX, 1.0, 1, &baseLine);

    int y = (rgb.rows - label_size.height) / 2;
    int x = (rgb.cols - label_size.width) / 2;

    cv::rectangle(rgb, cv::Rect(cv::Point(x, y), cv::Size(label_size.width, label_size.height + baseLine)),
                  cv::Scalar(255, 255, 255), -1);

    cv::putText(rgb, text, cv::Point(x, y + label_size.height),
                cv::FONT_HERSHEY_SIMPLEX, 1.0, cv::Scalar(0, 0, 0));

    return 0;
}

static int draw_fps(cv::Mat& rgb)
{
    // resolve moving average
    float avg_fps = 0.f;
    {
        static double t0 = 0.f;
        static float fps_history[10] = {0.f};

        double t1 = ncnn::get_current_time();
        if (t0 == 0.f)
        {
            t0 = t1;
            return 0;
        }

        float fps = 1000.f / (t1 - t0);
        t0 = t1;

        for (int i = 9; i >= 1; i--)
        {
            fps_history[i] = fps_history[i - 1];
        }
        fps_history[0] = fps;

        if (fps_history[9] == 0.f)
        {
            return 0;
        }

        for (int i = 0; i < 10; i++)
        {
            avg_fps += fps_history[i];
        }
        avg_fps /= 10.f;
    }

    char text[32];
    sprintf(text, "FPS=%.2f", avg_fps);

    int baseLine = 0;
    cv::Size label_size = cv::getTextSize(text, cv::FONT_HERSHEY_SIMPLEX, 0.5, 1, &baseLine);

    int y = 0;
    int x = rgb.cols - label_size.width;

    cv::rectangle(rgb, cv::Rect(cv::Point(x, y), cv::Size(label_size.width, label_size.height + baseLine)),
                  cv::Scalar(255, 255, 255), -1);

    cv::putText(rgb, text, cv::Point(x, y + label_size.height),
                cv::FONT_HERSHEY_SIMPLEX, 0.5, cv::Scalar(0, 0, 0));

    return 0;
}

static Yolo* g_yolo = 0;
static ncnn::Mutex lock;

class MyNdkCamera : public NdkCameraWindow
{
public:
    virtual void on_image_render(cv::Mat& rgb) const;
};

void MyNdkCamera::on_image_render(cv::Mat& rgb) const
{
    // nanodet
    {
        ncnn::MutexLockGuard g(lock);

        if (g_yolo)
        {
            std::vector<Object> objects;
            g_yolo->detect(rgb, objects);

            g_yolo->draw(rgb, objects);
        }
        else
        {
            draw_unsupported(rgb);
        }
    }

    draw_fps(rgb);
}
class MyNdkFunction
{
public:
    virtual void image_render(cv::Mat& rgb) const;
};

void MyNdkFunction::image_render(cv::Mat& rgb) const
{
    // nanodet
    {
        ncnn::MutexLockGuard g(lock);

        if (g_yolo)
        {
            std::vector<Object> objects;
            g_yolo->detect(rgb, objects);

            g_yolo->draw(rgb, objects);
        }
        else
        {
            draw_unsupported(rgb);
        }
    }

    draw_fps(rgb);
}
static MyNdkCamera* g_camera = 0;

extern "C" {


JNIEXPORT jbyteArray JNICALL
Java_com_tencent_yolov8ncnn_Yolov8Ncnn_processImageWithMask(JNIEnv* env, jobject thiz, jbyteArray inputImage, jint width, jint height) {
    LOGI("Starting processImageWithMask...");

//    // 将输入的 jbyteArray 转换为 OpenCV 的 cv::Mat
//    jbyte* yuvData = env->GetByteArrayElements(inputImage, nullptr);
//    if (yuvData == nullptr) {
//        LOGI("Failed to get YUV data from input image");
//        return nullptr;
//    }
//    LOGI("Successfully retrieved YUV data from input image");
//
//    cv::Mat yuvMat(height + height / 2, width, CV_8UC1, (unsigned char*)yuvData);
//    LOGI("Created YUV Mat with size: %d x %d", width, height + height / 2);
//
//    // 转换 YUV 到 RGB 格式
//    cv::Mat rgbMat;
//    cv::cvtColor(yuvMat, rgbMat, cv::COLOR_YUV2RGB_NV21);
//    LOGI("Converted YUV to RGB format");
//
//    // 调用 on_image_render 处理图像
//    MyNdkFunction camera;
//    camera.image_render(rgbMat);
//    LOGI("Processed image with on_image_render");
//
//    // 将处理后的 RGB 数据转换为 jbyteArray 传回 Java
//    int outputSize = rgbMat.total() * rgbMat.elemSize();
//    LOGI("Output RGB Mat size: %d bytes", outputSize);
//
//    jbyteArray outputImage = env->NewByteArray(outputSize);
//    if (outputImage == nullptr) {
//        LOGI("Failed to create output byte array");
//        env->ReleaseByteArrayElements(inputImage, yuvData, 0);
//        return nullptr;
//    }
//
//    env->SetByteArrayRegion(outputImage, 0, outputSize, (jbyte*)rgbMat.data);
//    LOGI("Set processed RGB data to output byte array");
//
//    // 释放资源
//    env->ReleaseByteArrayElements(inputImage, yuvData, 0);
//    LOGI("Released input YUV data");
//
//    LOGI("Finished processImageWithMask");
//    return outputImage;

//    // 1. 获取 YUV 数据
//    jbyte* yuvData = env->GetByteArrayElements(inputImage, nullptr);
//    if (yuvData == nullptr) {
//        LOGI("Failed to get YUV data from input image");
//        return nullptr;
//    }
//    LOGI("Successfully retrieved YUV data from input image");
//
//    // 2. 创建 YUV 格式的 cv::Mat
//    cv::Mat yuvMat(height + height / 2, width, CV_8UC1, (unsigned char*)yuvData);
//    LOGI("Created YUV Mat with size: %d x %d", width, height + height / 2);
//
//    // 3. YUV 到 RGB 转换（使用 NV21 格式）
//    cv::Mat rgbMat;
//    cv::cvtColor(yuvMat, rgbMat, cv::COLOR_YUV2RGB_NV21);
//    LOGI("Converted YUV to RGB format");
//
//    // 4. 处理图像 (on_image_render)
//    MyNdkFunction camera;
//    camera.image_render(rgbMat);
//    LOGI("Processed image with on_image_render");
//
//    // 5. 将 RGB 数据转换为 jbyteArray 传回 Java
//    int outputSize = rgbMat.total() * rgbMat.elemSize();
//    LOGI("Output RGB Mat size: %d bytes", outputSize);
//
//    jbyteArray outputImage = env->NewByteArray(outputSize);
//    if (outputImage == nullptr) {
//        LOGI("Failed to create output byte array");
//        env->ReleaseByteArrayElements(inputImage, yuvData, 0);
//        return nullptr;
//    }
//
//    // 6. 将数据写入 outputImage
//    env->SetByteArrayRegion(outputImage, 0, outputSize, (jbyte*)rgbMat.data);
//    LOGI("Set processed RGB data to output byte array");
//
//    // 7. 释放资源
//    env->ReleaseByteArrayElements(inputImage, yuvData, 0);
//    LOGI("Released input YUV data");
//
//    LOGI("Finished processImageWithMask");
//    return outputImage;
    LOGI("Starting processImageWithMask with RGB data...");

    // 1. 获取 RGB 数据
    jbyte* rgbData = env->GetByteArrayElements(inputImage, nullptr);
    if (rgbData == nullptr) {
        LOGI("Failed to get RGB data from input image");
        return nullptr;
    }
    LOGI("Successfully retrieved RGB data from input image");

    // 2. 创建 RGB 格式的 cv::Mat
    cv::Mat rgbMat(height, width, CV_8UC3, (unsigned char*)rgbData);
    LOGI("Created RGB Mat with size: %d x %d", width, height);

    // 3. 处理图像 (直接处理 RGB 数据)
    MyNdkFunction camera;
    camera.image_render(rgbMat);  // 调用处理函数
    LOGI("Processed image with on_image_render");

    // 4. 将处理后的 RGB 数据转换为 jbyteArray 传回 Java
    int outputSize = rgbMat.total() * rgbMat.elemSize();
    LOGI("Output RGB Mat size: %d bytes", outputSize);

    jbyteArray outputImage = env->NewByteArray(outputSize);
    if (outputImage == nullptr) {
        LOGI("Failed to create output byte array");
        env->ReleaseByteArrayElements(inputImage, rgbData, 0);
        return nullptr;
    }

    // 5. 将处理后的 RGB 数据写入 outputImage
    env->SetByteArrayRegion(outputImage, 0, outputSize, (jbyte*)rgbMat.data);
    LOGI("Set processed RGB data to output byte array");

    // 6. 释放资源
    env->ReleaseByteArrayElements(inputImage, rgbData, 0);
    LOGI("Released input RGB data");

    LOGI("Finished processImageWithMask");
    return outputImage;
}
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "JNI_OnLoad");

    g_camera = new MyNdkCamera;

    return JNI_VERSION_1_4;
}

JNIEXPORT void JNI_OnUnload(JavaVM* vm, void* reserved)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "JNI_OnUnload");

    {
        ncnn::MutexLockGuard g(lock);

        delete g_yolo;
        g_yolo = 0;
    }

    delete g_camera;
    g_camera = 0;
}

// public native boolean loadModel(AssetManager mgr, int modelid, int cpugpu);
JNIEXPORT jboolean JNICALL Java_com_tencent_yolov8ncnn_Yolov8Ncnn_loadModel(JNIEnv* env, jobject thiz, jobject assetManager, jint modelid, jint cpugpu)
{
    if (modelid < 0 || modelid > 6 || cpugpu < 0 || cpugpu > 1)
    {
        return JNI_FALSE;
    }

    AAssetManager* mgr = AAssetManager_fromJava(env, assetManager);

    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "loadModel %p", mgr);

    const char* modeltypes[] =
            {
                    "n",
                    "s",

            };

    const int target_sizes[] =
            {
                    640,
                    640,
            };

    const float mean_vals[][3] =
            {
                    {103.53f, 116.28f, 123.675f},
                    {103.53f, 116.28f, 123.675f},
            };

    const float norm_vals[][3] =
            {
                    { 1 / 255.f, 1 / 255.f, 1 / 255.f },
                    { 1 / 255.f, 1 / 255.f, 1 / 255.f },
            };

    const char* modeltype = modeltypes[(int)modelid];
    int target_size = target_sizes[(int)modelid];
    bool use_gpu = (int)cpugpu == 1;

    // reload
    {
        ncnn::MutexLockGuard g(lock);

        if (use_gpu && ncnn::get_gpu_count() == 0)
        {
            // no gpu
            delete g_yolo;
            g_yolo = 0;
        }
        else
        {
            if (!g_yolo)
                g_yolo = new Yolo;
            g_yolo->load(mgr, modeltype, target_size, mean_vals[(int)modelid], norm_vals[(int)modelid], use_gpu);
        }
    }

    return JNI_TRUE;
}

// public native boolean openCamera(int facing);
JNIEXPORT jboolean JNICALL Java_com_tencent_yolov8ncnn_Yolov8Ncnn_openCamera(JNIEnv* env, jobject thiz, jint facing)
{
    if (facing < 0 || facing > 1)
        return JNI_FALSE;

    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "openCamera %d", facing);

    g_camera->open((int)facing);

    return JNI_TRUE;
}

// public native boolean closeCamera();
JNIEXPORT jboolean JNICALL Java_com_tencent_yolov8ncnn_Yolov8Ncnn_closeCamera(JNIEnv* env, jobject thiz)
{
    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "closeCamera");

    g_camera->close();

    return JNI_TRUE;
}

// public native boolean setOutputWindow(Surface surface);
JNIEXPORT jboolean JNICALL Java_com_tencent_yolov8ncnn_Yolov8Ncnn_setOutputWindow(JNIEnv* env, jobject thiz, jobject surface)
{
    ANativeWindow* win = ANativeWindow_fromSurface(env, surface);

    __android_log_print(ANDROID_LOG_DEBUG, "ncnn", "setOutputWindow %p", win);

    g_camera->set_window(win);

    return JNI_TRUE;
}

}
