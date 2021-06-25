@file:JvmName("CommUtils")
@file:JvmMultifileClass

package com.waitou.wisdom_impl.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.provider.Settings

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

fun launchAppDetailsSettings(context: Context) {
    context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.parse("package:${context.packageName}")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    })
}
