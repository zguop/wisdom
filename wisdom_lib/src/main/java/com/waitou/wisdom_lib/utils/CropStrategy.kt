package com.waitou.wisdom_lib.utils

import android.content.Intent
import androidx.fragment.app.Fragment
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.WisdomConfig

/**
 * auth aboom
 * date 2020/7/1
 */
class CropStrategy {

    companion object {
        const val CROP_REQUEST = 0X22
    }

    private lateinit var media: Media

    internal fun startCrop(fragment: Fragment, media: Media) {
        this.media = media
        WisdomConfig.getInstance().cropEngine?.onStartCrop(fragment, media.uri, CROP_REQUEST)
    }

    internal fun cropResult(data: Intent?): Media {
        val cropUri = WisdomConfig.getInstance().cropEngine?.onCropResult(data)
        return media.also { it.cropUri = cropUri }
    }
}