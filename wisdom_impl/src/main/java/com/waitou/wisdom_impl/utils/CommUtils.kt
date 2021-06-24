@file:JvmName("CommUtils")
@file:JvmMultifileClass

package com.waitou.wisdom_impl.utils

import android.content.Context
import android.util.TypedValue

/**
 * auth aboom
 * date 2021/6/23
 */
fun Int.tdp(context: Context): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), context.resources.displayMetrics).toInt()
}