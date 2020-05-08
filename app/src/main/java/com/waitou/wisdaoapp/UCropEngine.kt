package com.waitou.wisdaoapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.blankj.utilcode.util.AppUtils
import com.blankj.utilcode.util.Utils
import com.waitou.wisdom_lib.utils.CameraStrategy
import com.yalantis.ucrop.UCrop
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * auth aboom
 * date 2019-06-14
 */
class UCropEngine {

    fun onStartCrop(sojourn: Activity, uri: Uri, requestCode: Int) {
        val file =
            CameraStrategy.getImageFileExistsAndCreate(Utils.getApp(),"image", "IMAGE_%s.jpg") ?: getCacheFileImagePath(sojourn)
        val destUri = Uri.Builder()
            .scheme("file")
            .appendPath(file.absolutePath)
            .build()
        Log.e("aa", "UCropEngine onStartCrop uri = $uri destUri = $destUri")
        UCrop.of(uri, destUri).start(sojourn, requestCode)
    }

    fun onCropResult(data: Intent): Uri? {
        val output = UCrop.getOutput(data)
        Log.e("aa", "UCropEngine onCropResult uri = $output ")

        return output
    }

    private fun getCacheFileImagePath(context: Context): File {
        val fileName = String.format(
            "IMAGE_%s.jpg",
            SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
                .format(Date())
        )
        val storageDir = File("${context.cacheDir}${File.separator}image")
        storageDir.mkdirs()
        return File(storageDir, fileName)
    }
}
