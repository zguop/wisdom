package com.waitou.wisdom_lib.utils

import android.content.res.Configuration
import android.content.res.Resources
import com.waitou.wisdom_lib.config.TYPE_IMAGE
import com.waitou.wisdom_lib.config.TYPE_VIDEO
import com.waitou.wisdom_lib.config.WisdomConfig

/**
 * auth aboom
 * date 2019-06-03
 */

/**
 * is a radio
 */
fun isSingleImage(): Boolean {
    return WisdomConfig.getInstance().maxSelectLimit == 1
}

/**
 * picture type
 */
fun onlyImages(): Boolean {
    return WisdomConfig.getInstance().mimeType == TYPE_IMAGE
}

/**
 * video type
 */
fun onlyVideos(): Boolean {
    return WisdomConfig.getInstance().mimeType == TYPE_VIDEO
}

/**
 * get the right image size
 */
fun getScreenImageResize(): Int {
    return when (Resources.getSystem().configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK) {
        Configuration.SCREENLAYOUT_SIZE_SMALL -> 100
        Configuration.SCREENLAYOUT_SIZE_NORMAL -> 180
        Configuration.SCREENLAYOUT_SIZE_LARGE -> 320
        else -> 180
    }
}