package com.waitou.wisdom_lib.call

import android.os.Bundle
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.ui.WisPreViewActivity

/**
 * auth aboom
 * date 2019-06-01
 */
interface OnMediaListener {
    /**
     * 跳转到预览页面
     * @param clazz 目标页面 继承WisPreViewActivity
     * @param selectMedias 选中的list
     * @param currentPosition 起始位置
     * @param albumId 相册id
     * @param bundle 自己实现页面的设置的属性
     */
    fun nextToPreView(
        clazz: Class<out WisPreViewActivity>,
        selectMedias: List<Media>,
        currentPosition: Int = 0,
        albumId: String = "",
        bundle: Bundle? = null
    )

    /**
     * 预览页面回调
     */
    fun onPreViewResult(resultMedias: List<Media>)

    /**
     * 退出回调数据
     */
    fun onResultFinish(resultMedias: List<Media>)
}
