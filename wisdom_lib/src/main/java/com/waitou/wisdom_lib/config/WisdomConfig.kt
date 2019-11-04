package com.waitou.wisdom_lib.config

import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.call.ImageEngine

/**
 * auth aboom
 * date 2019-05-30
 *
 *  可以选择的图片大小
 *  显示多少秒的内视频
 *  压缩图片
 *  多少kb以内的图片不进行压缩
 *  是否显示gif
 */
class WisdomConfig private constructor() {

    var mimeType = ofAll()
    var isCamera = true
    var authorities = ""
    var directory: String? = null
    var maxSelectLimit = 1
    var iImageEngine: ImageEngine? = null
    var imgMedias:List<Media>? = null

    fun reset() {
        mimeType = ofAll()
        isCamera = true
        authorities = ""
        directory = null
        maxSelectLimit = 1
        iImageEngine = null
        imgMedias = null
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
