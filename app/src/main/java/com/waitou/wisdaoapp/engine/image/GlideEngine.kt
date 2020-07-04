package com.waitou.wisdaoapp.engine.image

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import com.waitou.wisdaoapp.R
import com.waitou.wisdom_lib.call.ImageEngine

/**
 * auth aboom
 * date 2019-06-03
 */
class GlideEngine : ImageEngine {
    override fun displayAlbum(target: ImageView, uri: Uri, w: Int, h: Int, isGif: Boolean) {
        Glide.with(target)
                .asBitmap()
                .load(uri)
                .apply(
                        RequestOptions()
                                .centerCrop()
                                .override(w, h)
                )
                .into(target)
    }

    override fun displayThumbnail(target: ImageView, uri: Uri, w: Int, h: Int, isGif: Boolean) {
        Glide.with(target).load(uri)
                .transition(DrawableTransitionOptions.with(DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()))
                .apply(
                        RequestOptions()
                                .override(w, h)
                                .centerCrop()
                                .placeholder(R.drawable.place_holder))
                .into(target)
    }

    override fun displayPreviewImage(target: ImageView, uri: Uri, w: Int, h: Int, isGif: Boolean) {
        Glide.with(target).load(uri)
                .transition(DrawableTransitionOptions.with(DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()))
                .apply(
                        RequestOptions()
                                .override(w, h))
                .into(target)
    }
}
