package com.waitou.wisdom_lib

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.WisdomBuilder
import com.waitou.wisdom_lib.config.WisdomPreviewBuilder
import com.waitou.wisdom_lib.config.ofAll
import java.lang.ref.WeakReference

/**
 * auth aboom
 * date 2019-05-30
 */
class Wisdom private constructor(sojourn: Any) {

    internal val sojournReference: WeakReference<Any> = WeakReference(sojourn)

    fun config(mineType: Int = ofAll()): WisdomBuilder {
        return WisdomBuilder(this, mineType)
    }

    fun preview(): WisdomPreviewBuilder {
        return WisdomPreviewBuilder(this)
    }

    companion object {
        /**
         * 选择完成后回调数据的key
         */
        const val EXTRA_RESULT_SELECTION = "extra_result_selection"

        /**
         * 是否勾选原图
         */
        const val EXTRA_RESULT_FULL_IMAGE = "extra_result_full_image"

        @JvmStatic
        fun obtainResult(data: Intent): List<Media> {
            return data.getParcelableArrayListExtra<Media>(EXTRA_RESULT_SELECTION).orEmpty()
        }

        @JvmStatic
        fun isFullImage(data: Intent): Boolean {
            return data.getBooleanExtra(EXTRA_RESULT_FULL_IMAGE, false)
        }

        @JvmStatic
        fun of(activity: Activity): Wisdom {
            return Wisdom(activity)
        }

        @JvmStatic
        fun of(fragment: Fragment): Wisdom {
            return Wisdom(fragment)
        }
    }
}
