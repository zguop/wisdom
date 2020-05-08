package com.waitou.wisdom_lib.call

import android.content.Context
import com.waitou.wisdom_lib.bean.Media

/**
 * auth aboom
 * date 2020/5/8
 */
interface CompressEngine {
    fun compress(context: Context, medias: List<Media>, function: (List<String>) -> Unit)
}