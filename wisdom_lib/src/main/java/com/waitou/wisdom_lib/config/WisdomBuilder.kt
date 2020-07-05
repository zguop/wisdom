package com.waitou.wisdom_lib.config

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import com.waitou.wisdom_lib.Wisdom
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.call.CompressEngine
import com.waitou.wisdom_lib.call.CropEngine
import com.waitou.wisdom_lib.call.ImageEngine
import com.waitou.wisdom_lib.ui.WisdomWallActivity

/**
 * auth aboom
 * date 2019-05-31
 */
class WisdomBuilder(private val wisdom: Wisdom, mimeType: Int) {
    private val wisdomConfig = WisdomConfig.getResetInstance()

    init {
        wisdomConfig.mimeType = mimeType
    }

    /**
     * open camera default is true
     */
    fun isCamera(isCamera: Boolean): WisdomBuilder {
        wisdomConfig.isCamera = isCamera
        return this
    }

    /**
     * configuring image storage authorization
     * @param authorities "xxx.fileprovider"
     */
    @JvmOverloads
    fun fileProvider(authorities: String, directory: String? = null): WisdomBuilder {
        wisdomConfig.authorities = authorities
        wisdomConfig.directory = directory
        return this
    }

    /**
     * 文件选择数量
     * file selector number default 1
     */
    fun selectLimit(maxSelectLimit: Int): WisdomBuilder {
        if (maxSelectLimit < 1) {
            throw IllegalArgumentException("maxSelectLimit not less than one")
        }
        wisdomConfig.maxSelectLimit = maxSelectLimit
        return this
    }

    /**
     * 图片加载
     * image loading engine, need to be Implemented
     */
    fun imageEngine(iImageEngine: ImageEngine): WisdomBuilder {
        wisdomConfig.imageEngine = iImageEngine
        return this
    }

    /**
     * 图片裁剪
     * crop engine
     */
    fun cropEngine(cropEngine: CropEngine?): WisdomBuilder {
        wisdomConfig.cropEngine = cropEngine
        return this
    }

    /**
     * 图片压缩
     * image compress engine
     */
    fun compressEngine(compressEngine: CompressEngine?): WisdomBuilder {
        wisdomConfig.compressEngine = compressEngine
        return this
    }

    /**
     * 默认选中的图片
     * default selector medias
     */
    fun setMedias(medias: List<Media>?): WisdomBuilder {
        wisdomConfig.imgMedias = medias
        return this
    }

    /**
     * 选择文件不能超过 maxFileSize，限制文件选择 1 * 1024 * 1024 = 1M
     * selector maxSize file
     */
    fun filterMaxFileSize(maxFileSize: Int?): WisdomBuilder {
        wisdomConfig.filterMaxFileSize = maxFileSize
        return this
    }

    /**
     * start to select media and wait for result.
     */
    fun forResult(requestCode: Int, clazz: Class<out WisdomWallActivity>) {
        val o = wisdom.sojournReference.get()
        if (o is Fragment) {
            val activity = o.activity
            val intent = Intent(activity, clazz)
            o.startActivityForResult(intent, requestCode)
        }
        if (o is Activity) {
            val intent = Intent(o, clazz)
            o.startActivityForResult(intent, requestCode)
        }
    }
}
