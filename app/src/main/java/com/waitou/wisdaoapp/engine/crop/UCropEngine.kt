package com.waitou.wisdaoapp.engine.crop

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.blankj.utilcode.util.Utils
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.interfaces.CropEngine
import com.waitou.wisdom_lib.utils.CameraStrategy
import com.yalantis.ucrop.UCrop

/**
 * auth aboom
 * date 2019-06-14
 */
class UCropEngine : CropEngine {

    override fun onStartCrop(sojourn: Fragment, media: Media): Int {
        val file = CameraStrategy.createImageFile(Utils.getApp(), "IMAGE_%s.jpg", "image")
        Log.e("aa", "UCropEngine onStartCrop uri = ${media.uri} destUri = $file")
        UCrop.of(media.uri, Uri.fromFile(file)).start(sojourn.requireActivity(), sojourn)
        return UCrop.REQUEST_CROP
    }

    override fun onCropResult(data: Intent?, media: Media) {
        data?.let {
            val output = UCrop.getOutput(it)
            media.cropUri = output
            Log.e("aa", "UCropEngine onCropResult uri = ${media.cropUri} ")
        }
    }
}
