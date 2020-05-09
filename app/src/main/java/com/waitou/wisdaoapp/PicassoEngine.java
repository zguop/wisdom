package com.waitou.wisdaoapp;

import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.waitou.wisdom_lib.call.ImageEngine;

import org.jetbrains.annotations.NotNull;

/**
 * auth aboom
 * date 2019-06-13
 */
public class PicassoEngine implements ImageEngine {

    @Override
    public void displayAlbum(@NotNull ImageView target, @NotNull Uri uri, int w, int h, boolean isGif) {
        Picasso.get().load(uri).resize(w, h).centerCrop().into(target);
    }

    @Override
    public void displayThumbnail(@NotNull ImageView target, @NotNull Uri uri, int w, int h, boolean isGif) {
        Picasso.get().load(uri).resize(w, h).centerCrop().placeholder(R.drawable.place_holder).into(target);
    }

    @Override
    public void displayPreviewImage(@NotNull ImageView target, @NotNull Uri uri, int w, int h, boolean isGif) {
        Picasso.get().load(uri).resize(w, h).centerInside().into(target);


    }
}
