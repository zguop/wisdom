package com.waitou.wisdaoapp

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.waitou.wisdom_lib.call.IImageEngine

/**
 * auth aboom
 * date 2019-06-03
 */
class GlideEngine : IImageEngine {
    override fun displayImage(target: ImageView, uri: Uri, w: Int, h: Int) {

        Glide.with(target)
                .load(uri)
                .apply(
                        RequestOptions()
                                .override(w, h)
                )
                .into(target)
    }


}
