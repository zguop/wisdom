package com.waitou.wisdom_lib.config

import android.webkit.MimeTypeMap

/**
 * auth aboom
 * date 2019-05-31
 */
const val TYPE_IMAGE = 0x01
const val TYPE_VIDEO = 0X02

/**
 * all image type
 */
fun ofAll(): Int {
    return TYPE_IMAGE or TYPE_VIDEO
}

/**
 * load only image
 */
fun ofImage(): Int {
    return TYPE_IMAGE
}

/**
 * load only image
 */
fun ofVideo(): Int {
    return TYPE_VIDEO
}

fun isImage(mediaType: String): Boolean {
    return mediaType.startsWith("image")
}

fun isVideo(mediaType: String): Boolean {
    return mediaType.startsWith("video")
}

fun isGif(mediaType: String): Boolean {
    return mediaType.equals("image/gif", true)
}

fun getMimeType(path: String): String {
    val extension = MimeTypeMap.getFileExtensionFromUrl(path)
    //使用系统API，获取MimeTypeMap的单例实例，然后调用其内部方法获取文件后缀名（扩展名）所对应的MIME类型
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase()) ?: "image/jpeg"
}

