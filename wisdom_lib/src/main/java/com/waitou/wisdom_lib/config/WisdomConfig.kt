package com.waitou.wisdom_lib.config

import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.interfaces.CompressEngine
import com.waitou.wisdom_lib.interfaces.CropEngine
import com.waitou.wisdom_lib.interfaces.ImageEngine

/**
 * auth aboom
 * date 2019-05-30
 *
 *  可以选择的图片大小
 *  显示多少秒的内视频
 *  是否显示gif
 */
class WisdomConfig private constructor() {

    var mediaType = ofAll()
    var isCamera = true
    var authorities = ""
    var directory: String? = null
    var maxSelectLimit = 1
    var imageEngine: ImageEngine? = null
    var compressEngine: CompressEngine? = null
    var cropEngine: CropEngine? = null
    var imgMedias: List<Media>? = null
    var filterImageMaxSize: Int? = null //最大选择的图片大小，超过该大小的图片不展示
    var filterVideoMaxSize: Int? = null //最大选择的视频大小，超过该大小的视频不展示
    var mimeTypeSet: Set<String>? = null //限定文件类型 'image/gif','image/png'
    var isFilterMimeTypeSet = true // mimeTypeSet 过滤

    fun reset() {
        mediaType = ofAll()
        isCamera = true
        authorities = ""
        directory = null
        maxSelectLimit = 1
        imageEngine = null
        compressEngine = null
        cropEngine = null
        imgMedias = null
        filterImageMaxSize = null
        filterVideoMaxSize = null
        mimeTypeSet = null
        isFilterMimeTypeSet = true
    }

    companion object {
        fun getInstance(): WisdomConfig {
            return InstanceHolder.INSTANCE
        }

        internal fun getResetInstance(): WisdomConfig {
            val instance = getInstance()
            instance.reset()
            return instance
        }
    }

    private object InstanceHolder {
        val INSTANCE = WisdomConfig()
    }
}
