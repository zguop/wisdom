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

    override fun onStartCrop(sojourn: Fragment, uri: Uri, requestCode: Int) {
        val file = CameraStrategy.createImageFile(Utils.getApp(), "IMAGE_%s.jpg", "image")
        Log.e("aa", "UCropEngine onStartCrop uri = $uri destUri = $file")
        UCrop.of(uri, Uri.fromFile(file)).start(sojourn.requireActivity(), sojourn, requestCode)
    }

    override fun onCropResult(data: Intent?): Uri? {
        val output = UCrop.getOutput(data!!)
        Log.e("aa", "UCropEngine onCropResult uri = $output ")
        return output
    }
}
