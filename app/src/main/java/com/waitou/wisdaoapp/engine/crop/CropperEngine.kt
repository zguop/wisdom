package com.waitou.wisdaoapp.engine.crop

import android.content.Intent
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

    override fun onStartCrop(sojourn: Fragment, media: Media): Int {
        CropImage.activity(media.uri).start(sojourn.requireActivity(), sojourn)
        Log.e("aa", "CropperEngine onStartCrop uri = ${media.uri}")
        return CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
    }

    override fun onCropResult(data: Intent?, media: Media) {
        val activityResult = CropImage.getActivityResult(data)
        val uri = activityResult.uri
        Log.e("aa", "CropperEngine onCropResult uri = $uri")
        val originalUri = activityResult.originalUri
        Log.e("aa", "CropperEngine onCropResult originalUri = $originalUri")
        media.cropPath = uri.path
    }
}