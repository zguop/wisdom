package com.waitou.wisdom_impl.view

import android.content.Context
import android.util.AttributeSet
import android.widget.RelativeLayout

/**
 * Created by waitou on 17/2/8.
 * 矩形RelativeLayout
 */

open class SquareRelativeLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    RelativeLayout(context, attrs, defStyle) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec)
    }
}
