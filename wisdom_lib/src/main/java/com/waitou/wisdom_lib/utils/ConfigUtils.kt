@file:JvmName("ConfigUtils")
@file:JvmMultifileClass

package com.waitou.wisdom_lib.utils

import android.content.res.Configuration
import android.content.res.Resources
import com.waitou.wisdom_lib.R
import com.waitou.wisdom_lib.config.*

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
    return WisdomConfig.getInstance().mediaType == ofImage()
}

/**
 * video type
 */
fun onlyVideos(): Boolean {
    return WisdomConfig.getInstance().mediaType == ofVideo()
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

fun isFilterMimeTypeSet() = WisdomConfig.getInstance().isFilterMimeTypeSet
inline fun hasMineTypeSet(block: (Set<String>) -> Unit) = WisdomConfig.getInstance().mimeTypeSet?.let(block)
inline fun hasImageMaxSizeConfig(block: (Int) -> Unit) = WisdomConfig.getInstance().filterImageMaxSize?.let(block)
inline fun hasVideoMaxSizeConfig(block: (Int) -> Unit) = WisdomConfig.getInstance().filterVideoMaxSize?.let(block)

