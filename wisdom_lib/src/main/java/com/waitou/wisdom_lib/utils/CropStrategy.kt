package com.waitou.wisdom_lib.utils

import android.content.Intent
import android.support.v4.app.Fragment
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.WisdomConfig

/**
 * auth aboom
 * date 2020/7/1
 */
class CropStrategy {

    private lateinit var media: Media
    var startCropRequest: Int? = null
        internal set

    fun startCrop(fragment: Fragment, media: Media) {
        this.media = media
        this.startCropRequest = WisdomConfig.getInstance().cropEngine?.onStartCrop(
            fragment,
            media
        )
    }

    fun cropResult(data: Intent?): Media {
        WisdomConfig.getInstance().cropEngine?.onCropResult(data, media)
        return media
    }
}