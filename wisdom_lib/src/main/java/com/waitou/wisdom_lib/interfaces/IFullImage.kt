package com.waitou.wisdom_lib.interfaces

import android.os.Bundle
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.ui.WisPreViewActivity

/**
 * auth aboom
 * date 2019-06-01
 */
interface IFullImage {

    /**
     * 是否是原图
     */
    fun isFullImage(): Boolean

    /**
     * 同步原图状态
     */
    fun setFullImage(fullImage: Boolean)
}
