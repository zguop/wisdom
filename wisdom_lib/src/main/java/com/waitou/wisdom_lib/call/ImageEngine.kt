package com.waitou.wisdom_lib.call

import android.net.Uri
import android.widget.ImageView

/**
 * auth aboom
 * date 2019-06-03
 */
interface ImageEngine {
    fun displayImage(target: ImageView, uri: Uri, w: Int, h: Int)
}
