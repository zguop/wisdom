package com.waitou.wisdom_lib.interfaces

import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.waitou.wisdom_lib.bean.Media

/**
 * auth aboom
 * date 2020/7/1
 */
interface CropEngine {

    /**
     * 开始裁剪
     *
     * @param sojourn Jump and crop through fragment
     */
    fun onStartCrop(sojourn: Fragment, uri: Uri, requestCode: Int)

    /**
     * 裁剪结束
     *
     * @param data Intent data
     * @param media Assign value to cropPath
     */
    fun onCropResult(data: Intent?): Uri?
}