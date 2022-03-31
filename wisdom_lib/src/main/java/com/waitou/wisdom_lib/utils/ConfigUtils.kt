@file:JvmName("ConfigUtils")
@file:JvmMultifileClass

package com.waitou.wisdom_lib.utils

import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import com.waitou.wisdom_lib.config.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.R)
fun isAndroidR():Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
fun isAndroidQ(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

fun isSingleImage(): Boolean = WisdomConfig.getInstance().maxSelectLimit == 1
fun onlyImages() = WisdomConfig.getInstance().mediaType == ofImage()
fun onlyVideos() = WisdomConfig.getInstance().mediaType == ofVideo()
fun isFilterMimeTypeSet() = WisdomConfig.getInstance().isFilterMimeTypeSet
inline fun hasMineTypeSet(block: (Set<String>) -> Unit) = WisdomConfig.getInstance().mimeTypeSet?.let(block)
inline fun hasImageMaxSizeConfig(block: (Long) -> Unit) = WisdomConfig.getInstance().filterImageMaxSize?.let(block)
inline fun hasVideoMaxSizeConfig(block: (Long) -> Unit) = WisdomConfig.getInstance().filterVideoMaxSize?.let(block)

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