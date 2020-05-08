package com.waitou.wisdaoapp;

import android.content.Context;
import android.graphics.Bitmap;

import com.waitou.wisdom_lib.bean.Media;
import com.waitou.wisdom_lib.call.CompressEngine;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileBatchCallback;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/**
 * auth aboom
 * date 2020/5/8
 */
public class TinyCompressEngine implements CompressEngine {

    @Override
    public void compress(@NotNull Context context, @NotNull final List<Media> medias, @NotNull final Function1<? super List<String>, Unit> function) {
        Tiny.FileCompressOptions compressOptions = new Tiny.FileCompressOptions();
        compressOptions.config = Bitmap.Config.ARGB_8888;
        String[] fileArray = new String[medias.size()];
        for (int i = 0; i < medias.size(); i++) {
            fileArray[i] = medias.get(i).getPath();
        }
        Tiny.getInstance().source(fileArray)
                .batchAsFile()
                .withOptions(compressOptions)
                .batchCompress(new FileBatchCallback() {
                    @Override
                    public void callback(boolean isSuccess, String[] outfiles, Throwable t) {

                        for (int i = 0; i < outfiles.length; i++) {
                            Media media = medias.get(i);
                            if (media.isGif()) {
                                outfiles[i] = media.getPath();
                            }
                            if (outfiles[i] == null) {
                                outfiles[i] = media.getPath();
                            }
                        }
                        function.invoke(isSuccess ? Arrays.asList(outfiles) : null);
                    }
                });
    }
}
