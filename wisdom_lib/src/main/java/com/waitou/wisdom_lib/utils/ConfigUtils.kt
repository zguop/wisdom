@file:JvmName("ConfigUtils")
@file:JvmMultifileClass

package com.waitou.wisdom_lib.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import com.waitou.wisdom_lib.config.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun isAndroidQ(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
fun isSingleImage(): Boolean = WisdomConfig.getInstance().maxSelectLimit == 1
fun onlyImages() = WisdomConfig.getInstance().mediaType == ofImage()
fun onlyVideos() = WisdomConfig.getInstance().mediaType == ofVideo()
fun isFilterMimeTypeSet() = WisdomConfig.getInstance().isFilterMimeTypeSet
inline fun hasMineTypeSet(block: (Set<String>) -> Unit) = WisdomConfig.getInstance().mimeTypeSet?.let(block)
inline fun hasImageMaxSizeConfig(block: (Int) -> Unit) = WisdomConfig.getInstance().filterImageMaxSize?.let(block)
inline fun hasVideoMaxSizeConfig(block: (Int) -> Unit) = WisdomConfig.getInstance().filterVideoMaxSize?.let(block)

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

/**
 * 获取视频的播放时长
 * content://media/external/images/media/3143
 */
fun getDuration(context: Context, uri: Uri): Long {
    return try {
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context.applicationContext, uri)
        return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0
    } catch (e: Exception) {
        0
    }
}

/**
 * 获取图片选择角度
 */
fun getRotateDegree(filePath: String): Int {
    return try {
        val exifInterface = ExifInterface(filePath)
        val orientation = exifInterface.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
    } catch (e: IOException) {
        0
    }
}

fun rotateImage(file: File) {
    val filePath = file.absolutePath
    val degree = getRotateDegree(filePath)
    if (degree > 0) {
        val options = BitmapFactory.Options()
        options.inSampleSize = 2
        options.inJustDecodeBounds = false

        val src = BitmapFactory.decodeFile(filePath, options)

        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val ref = Bitmap.createBitmap(src, 0, 0, src.width, src.height, matrix, true)

        BufferedOutputStream(FileOutputStream(filePath)).use {
            ref.compress(Bitmap.CompressFormat.JPEG, 80, it)
            it.flush()
        }
        src?.recycle()
        ref?.recycle()
    }
}