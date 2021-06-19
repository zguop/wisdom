@file:JvmName("MimeType")
@file:JvmMultifileClass
package com.waitou.wisdom_lib.config




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

