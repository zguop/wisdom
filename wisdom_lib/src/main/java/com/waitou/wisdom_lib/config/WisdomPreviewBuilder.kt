package com.waitou.wisdom_lib.config

import android.app.Activity
import android.support.v4.app.Fragment
import com.waitou.wisdom_lib.Wisdom
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.interfaces.ImageEngine
import com.waitou.wisdom_lib.ui.WisPreViewActivity

/**
 * auth aboom
 * date 2019-11-02
 */
class WisdomPreviewBuilder(private val wisdom: Wisdom) {

    private val wisdomConfig = WisdomConfig.getResetInstance()

    /**
     * image loading engine, need to be Implemented
     */
    fun imageEngine(iImageEngine: ImageEngine): WisdomPreviewBuilder {
        wisdomConfig.imageEngine = iImageEngine
        return this
    }

    fun setMedias(medias: List<Media>): WisdomPreviewBuilder {
        wisdomConfig.imgMedias = medias
        return this
    }

    fun setPaths(path: List<String>): WisdomPreviewBuilder {
        wisdomConfig.imgMedias = path.map { Media(0, "", it, 0, 0) }
        return this
    }

    fun go(clazz: Class<out WisPreViewActivity>, position: Int = 0) {
        requireNotNull(wisdomConfig.imgMedias) { "imgMedias is empty?" }
        val o = wisdom.sojournReference.get()
        if (o is Fragment) {
            val activity = o.activity
            activity?.let {
                val intent = WisPreViewActivity.getIntent(
                        activity,
                        clazz,
                        wisdomConfig.imgMedias!!,
                        position,
                        "",
                        false,
                        WisPreViewActivity.WIS_PREVIEW_MODULE_TYPE_VISIT
                )
                o.startActivity(intent)
            }
        }
        if (o is Activity) {
            val intent = WisPreViewActivity.getIntent(
                    o,
                    clazz,
                    wisdomConfig.imgMedias!!,
                    position,
                    "",
                    false,
                    WisPreViewActivity.WIS_PREVIEW_MODULE_TYPE_VISIT
            )
            o.startActivity(intent)
        }
    }
}
