package com.waitou.wisdom_lib.call

import android.content.Intent
import android.support.v4.app.Fragment
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
     * @param media get media.uri or media.path
     * @return requestCode
     */
    fun onStartCrop(sojourn: Fragment, media: Media): Int

    /**
     * 裁剪结束
     *
     * @param data Intent data
     * @param media Assign value to cropPath
     */
    fun onCropResult(data: Intent?, media: Media)
}