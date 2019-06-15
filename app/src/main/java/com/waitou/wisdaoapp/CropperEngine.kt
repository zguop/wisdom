package com.waitou.wisdaoapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.theartofdev.edmodo.cropper.CropImage

/**
 * auth aboom
 * date 2019-06-15
 */
class CropperEngine {
    fun onStartCrop(sojourn: Activity, uri: Uri) {
        Log.e("aa" , "CropperEngine onStartCrop uri = $uri")
        CropImage.activity(uri).start(sojourn)
    }
    fun onCropResult(data: Intent): Uri {
        val activityResult = CropImage.getActivityResult(data)
        val uri = activityResult.uri
        Log.e("aa" , "CropperEngine onCropResult uri = $uri")
        val originalUri = activityResult.originalUri
        Log.e("aa" , "CropperEngine onCropResult originalUri = $originalUri")
        return uri
    }
}