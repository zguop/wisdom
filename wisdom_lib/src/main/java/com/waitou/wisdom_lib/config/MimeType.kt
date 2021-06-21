@file:JvmName("MimeType")
@file:JvmMultifileClass

package com.waitou.wisdom_lib.config


const val MIME_TYPE_IMAGE_JPEG = "image/jpeg"
const val MIME_TYPE_VIDEO_MP4 = "video/mp4"

const val TYPE_IMAGE = 0x01
const val TYPE_VIDEO = 0X02

/**
 * all image type
 */
fun ofAll(): Int = TYPE_IMAGE or TYPE_VIDEO

/**
 * load only image
 */
fun ofImage(): Int = TYPE_IMAGE

/**
 * load only image
 */
fun ofVideo(): Int = TYPE_VIDEO


fun isImage(mediaType: String): Boolean = mediaType.startsWith("image")

fun isVideo(mediaType: String): Boolean = mediaType.startsWith("video")

fun isGif(mediaType: String): Boolean = mediaType.equals("image/gif", true)

