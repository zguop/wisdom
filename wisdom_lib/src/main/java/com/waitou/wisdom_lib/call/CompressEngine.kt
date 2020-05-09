package com.waitou.wisdom_lib.call

import android.content.Context
import com.waitou.wisdom_lib.bean.Media

/**
 * auth aboom
 * date 2020/5/8
 * compress engine
 */
interface CompressEngine {
    /**
     * 压缩方法，
     * @param context 上下文
     * @param medias 数据 压缩后图片给 Media.compressPath赋值
     * @param function 通知我压缩完成
     */
    fun compress(context: Context, medias: List<Media>, function: () -> Unit)
}