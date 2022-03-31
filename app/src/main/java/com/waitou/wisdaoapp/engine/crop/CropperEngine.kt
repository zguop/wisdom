package com.waitou.wisdaoapp.engine.crop

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.theartofdev.edmodo.cropper.CropImage
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.interfaces.CropEngine

/**
 * auth aboom
 * date 2019-06-15
 */
class CropperEngine : CropEngine {

    override fun onStartCrop(sojourn: Fragment, uri: Uri, requestCode: Int) {
        Log.e("aa", "CropperEngine onStartCrop uri = ${uri}")
        val cropImage = CropImage.activity(uri)
        sojourn.startActivityForResult(cropImage.getIntent(sojourn.requireContext()), requestCode)
    }

    override fun onCropResult(data: Intent?): Uri {
        val activityResult = CropImage.getActivityResult(data)
        val uri = activityResult.uri
        Log.e("aa", "CropperEngine onCropResult uri = $uri")
        val originalUri = activityResult.originalUri
        Log.e("aa", "CropperEngine onCropResult originalUri = $originalUri")
        return uri
    }
}