@file:JvmName("CommUtils")
@file:JvmMultifileClass

package com.waitou.wisdom_impl.utils

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.provider.Settings
import com.waitou.wisdom_impl.R
import java.math.BigDecimal

/**
 * auth aboom
 * date 2021/6/23
 */
fun Int.dp2pxI(): Int {
    return dp2pxF().toInt()
}

fun Int.dp2pxF(): Float {
    return this * Resources.getSystem().displayMetrics.density + 0.5f
}

fun Context.obtainAttrRes(attr: Int, defaultRes: Int): Int {
    return obtainAttrRes(intArrayOf(attr), intArrayOf(defaultRes))[0]
}

fun Context.obtainAttrRes(attr: IntArray, defaultRes: IntArray): IntArray {
    val ta = theme.obtainStyledAttributes(attr)
    val intArray = IntArray(attr.size)
    defaultRes.forEachIndexed { index, i ->
        intArray[index] = ta.getResourceId(index, i)
    }
    ta.recycle()
    return intArray
}

fun launchAppDetailsSettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:${context.packageName}")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}

fun Long.formatSize(): String {
    val byte = this.toDouble()
    val kb = byte / 1024.0
    val mb = byte / 1024.0 / 1024.0
    val gb = byte / 1024.0 / 1024.0 / 1024.0
    val tb = byte / 1024.0 / 1024.0 / 1024.0 / 1024.0
    return when {
        tb >= 1 -> "${
            tb.toBigDecimal()
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .toDouble()
        } T"
        gb >= 1 -> "${
            gb.toBigDecimal()
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .toDouble()
        } G"
        mb >= 1 -> "${
            mb.toBigDecimal()
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .toDouble()
        } M"
        kb >= 1 -> "${
            kb.toBigDecimal()
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .toDouble()
        } K"
        else -> "${
            byte.toBigDecimal()
                .setScale(2, BigDecimal.ROUND_HALF_UP)
                .toDouble()
        } B"
    }
}