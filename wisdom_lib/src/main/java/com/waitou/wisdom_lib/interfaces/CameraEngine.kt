package com.waitou.wisdom_lib.interfaces

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment

/**
 * @author aboom
 * @date 2022/3/17
 */
interface CameraEngine {
    /**
     * 开始相机拍照
     */
    fun onStartCameraImage(fragment: Fragment, requestCode: Int)

    /**
     * 开始相机录像
     */
    fun onStartCameraVideo(fragment: Fragment, requestCode: Int)

    /**
     * 相机拍照结束
     * @return Uri filepath
     */
    fun onCameraResult(data: Intent?): Uri
}