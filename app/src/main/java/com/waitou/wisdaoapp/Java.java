package com.waitou.wisdaoapp;

import android.app.Activity;

import com.blankj.utilcode.util.AppUtils;
import com.waitou.wisdaoapp.engine.compress.TinyCompressEngine;
import com.waitou.wisdaoapp.engine.crop.UCropEngine;
import com.waitou.wisdaoapp.engine.image.GlideEngine;
import com.waitou.wisdom_impl.ui.PhotoWallActivity;
import com.waitou.wisdom_lib.Wisdom;
import com.waitou.wisdom_lib.config.MimeType;

/**
 * auth aboom
 * date 2020/7/5
 */
class Java {

    public static void main(Activity activity) {
        Wisdom.of(activity) // activity /fragment
                .config(MimeType.ofAll()) //图片选择类型
                .imageEngine(new GlideEngine()) //设置图片加载引擎
                .cropEngine(new UCropEngine()) // 设置图片裁剪引擎
                .compressEngine(new TinyCompressEngine()) //设置图片压缩引擎
                .fileProvider(AppUtils.getAppPackageName() + ".utilcode.provider", "image")
                .selectLimit(1)
                .isCamera(true)
                .forResult(101, PhotoWallActivity.class);
    }
}
