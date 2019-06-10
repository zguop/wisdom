package com.waitou.wisdom_lib.config

import com.waitou.wisdom_lib.call.ICropCall
import com.waitou.wisdom_lib.call.IImageEngine

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
    var cropCall: ICropCall? = null
    var maxSelectLimit = 1
    var iImageEngine: IImageEngine? = null

    fun reset() {
        mimeType = ofAll()
        isCamera = true
        authorities = ""
        directory = null
        cropCall = null
        maxSelectLimit = 1
        iImageEngine = null
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
