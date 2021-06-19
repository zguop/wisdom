package com.waitou.wisdom_lib.config

import android.app.Activity
import android.content.Intent
import android.support.v4.app.Fragment
import com.waitou.wisdom_lib.Wisdom
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.interfaces.CompressEngine
import com.waitou.wisdom_lib.interfaces.CropEngine
import com.waitou.wisdom_lib.interfaces.ImageEngine
import com.waitou.wisdom_lib.ui.WisdomWallActivity

/**
 * auth aboom
 * date 2019-05-31
 */
class WisdomBuilder(private val wisdom: Wisdom, mimeType: Int) {
    private val wisdomConfig = WisdomConfig.getResetInstance()

    init {
        wisdomConfig.mediaType = mimeType
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
        filterImageMaxFileSize(maxFileSize)
        filterVideoMaxFileSize(maxFileSize)
        return this
    }

    /**
     * 最大选择的图片大小，超过这个大小的图片不展示 1 * 1024 * 1024 = 1M
     * selector filterImageMaxSize file
     */
    fun filterImageMaxFileSize(maxFileSize: Int?): WisdomBuilder {
        wisdomConfig.filterImageMaxSize = maxFileSize
        return this
    }

    /**
     * 最大选择的视频大小，超过这个大小的视频不展示 1 * 1024 * 1024 = 1M
     * selector filterVideoMaxSize file
     */
    fun filterVideoMaxFileSize(maxFileSize: Int?): WisdomBuilder {
        wisdomConfig.filterVideoMaxSize = maxFileSize
        return this
    }

    /**
     * 限定文件类型，
     * mimeTypeSet=[image/gif,image/png] and isFilterMimeTypeSet true 列表中gif，png将不展示 false 列表中只展示gif，png图片
     *
     * 例：只选择gif
     * mimeTypeSet=[image/gif]
     * isFilterMimeTypeSet=false
     *
     * @param mimeTypeSet 必须符合 mineType 标准格式才有效
     * @param isFilterMimeTypeSet default true
     *
     */
    fun mimeTypeSet(mimeTypeSet: Set<String>?, isFilterMimeTypeSet: Boolean = true): WisdomBuilder {
        wisdomConfig.mimeTypeSet = mimeTypeSet
        wisdomConfig.isFilterMimeTypeSet = isFilterMimeTypeSet
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
