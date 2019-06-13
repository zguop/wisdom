package com.waitou.wisdom_lib.call

import android.net.Uri
import android.widget.ImageView

/**
 * auth aboom
 * date 2019-06-03
 */
interface ImageEngine {
    /**
     * 加载相册封面
     */
    fun displayAlbum(target: ImageView, uri: Uri, w: Int, h: Int, isGif: Boolean)

    /**
     * 加载相册缩略图
     */
    fun displayThumbnail(target: ImageView, uri: Uri, w: Int, h: Int, isGif: Boolean)

    /**
     * 加载预览界面图片
     */
    fun displayPreviewImage(target: ImageView, uri: Uri, w: Int, h: Int, isGif: Boolean)
}
