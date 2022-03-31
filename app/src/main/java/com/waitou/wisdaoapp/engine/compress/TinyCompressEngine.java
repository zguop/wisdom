package com.waitou.wisdaoapp.engine.compress;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.waitou.wisdom_lib.bean.Media;
import com.waitou.wisdom_lib.interfaces.CompressEngine;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.FileBatchCallback;

import java.io.File;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

/**
 * auth aboom
 * date 2020/5/8
 */
public class TinyCompressEngine implements CompressEngine {

    @Override
    public void compress(@NonNull Context context, @NonNull List<Media> medias, @NonNull Function0<Unit> function0) {
        Tiny.FileCompressOptions compressOptions = new Tiny.FileCompressOptions();
        compressOptions.config = Bitmap.Config.ARGB_8888;
        Uri[] fileArray = new Uri[medias.size()];
        for (int i = 0; i < medias.size(); i++) {
            fileArray[i] = medias.get(i).cropNullToUri();
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
                                    media.setCompressUri(Uri.fromFile(new File(outfiles[i])));
                                }
                            }
                        }
                        function0.invoke();
                    }
                });
    }
}
