package com.waitou.wisdaoapp.engine.camera;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.blankj.utilcode.util.AppUtils;
import com.waitou.wisdom_lib.interfaces.CameraEngine;
import com.waitou.wisdom_lib.utils.CameraStrategy;

/**
 * @author aboom
 * @date 2022/3/20
 */
public class CustomCameraEngine implements CameraEngine {

    /**
     * 例子中还是调用了系统相机，
     */
    private final CameraStrategy strategy = new CameraStrategy();

    @Override
    public void onStartCameraImage(@NonNull Fragment fragment, int requestCode) {
        Log.e("aa", "调用自定义拍照");
        strategy.startCameraImage(fragment, AppUtils.getAppPackageName() + ".utilcode.provider", AppUtils.getAppName());
    }

    @Override
    public void onStartCameraVideo(@NonNull Fragment fragment, int requestCode) {
        Log.e("aa", "调用自定义视频");
        strategy.startCameraVideo(fragment, AppUtils.getAppPackageName() + ".utilcode.provider", AppUtils.getAppName());
    }

    @NonNull
    @Override
    public Uri onCameraResult(@Nullable Intent data) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = strategy.fileUri;
        } else {
            uri = Uri.fromFile(strategy.filePath);
        }
        return uri;
    }
}
