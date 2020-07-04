package com.waitou.wisdaoapp.engine.compress;

import android.content.Context;
import android.graphics.Bitmap;

import com.blankj.utilcode.util.LogUtils;
import com.waitou.wisdom_lib.bean.Media;
import com.waitou.wisdom_lib.call.CompressEngine;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileBatchCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * auth aboom
 * date 2020/5/8
 */
public class TinyCompressEngine implements CompressEngine {

    @Override
    public void compress(@NotNull Context context, @NotNull final List<Media> medias, @NotNull final Function0<Unit> function) {
        Tiny.FileCompressOptions compressOptions = new Tiny.FileCompressOptions();
        compressOptions.config = Bitmap.Config.ARGB_8888;
        String[] fileArray = new String[medias.size()];
        for (int i = 0; i < medias.size(); i++) {
            fileArray[i] = medias.get(i).cropNullToPath();
        }
        LogUtils.e("开始压缩...");
        Tiny.getInstance().source(fileArray)
                .batchAsFile()
                .withOptions(compressOptions)
                .batchCompress(new FileBatchCallback() {
                    @Override
                    public void callback(boolean isSuccess, String[] outfiles, Throwable t) {
                        if (isSuccess) {
                            for (int i = 0; i < outfiles.length; i++) {
                                Media media = medias.get(i);
                                if (!media.isGif()) {
                                    media.setCompressPath(outfiles[i]);
                                }
                            }
                        }
                        function.invoke();
                    }
                });
    }
}
