package com.waitou.wisdom_impl.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import com.waitou.wisdom_impl.R
import kotlinx.android.synthetic.main.wis_pop_albums.view.*


class PopView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), Animation.AnimationListener {

    companion object {
        private const val DURATION = 350
        private const val DEFAULT_DIS = 0.65f
    }

    private var dismissListener: OnDismissListener? = null

    /**
     * 设置屏幕的最大占据比例
     * 屏幕的高度 * heightPercent - 140dp最小阴影
     * item最大高度超过则显示该最大比例值，否则wrap_content
     */
    private var heightPercent: Float = DEFAULT_DIS

    /**
     * 屏幕高度
     */
    private val heightPixels: Int = context.resources.displayMetrics.heightPixels

    /**
     * 是否正在 展开收起 播放动画
     */
    var isAnimation: Boolean = false
    /**
     * 是否显示
     */
    var isShowing: Boolean = false

    init {
        View.inflate(context, R.layout.wis_pop_albums, this@PopView)
            .viewOutSide.setOnClickListener {
            dismiss()
        }
    }

    fun setDisMissListener(listener: () -> Unit) {
        this.dismissListener = object : OnDismissListener {
            override fun onDismiss() {
                listener.invoke()
            }
        }
    }

    fun setDisMissListener(listener: OnDismissListener) {
        this.dismissListener = listener
    }

    /**
     * @param heightPercent default 0.75
     */
    fun setMaxDis(heightPercent: Float) {
        this.heightPercent = heightPercent
    }

    fun getContentView(): RecyclerView {
        return popList
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (heightPercent != 0f) {
            val layoutParams = popList.layoutParams
            val heightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            popList.measure(widthMeasureSpec, heightSpec)
            val planHeight = popList.measuredHeight
            val maxHeight = (heightPixels * heightPercent).toInt() - top
            if (planHeight > maxHeight) {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height = maxHeight
            } else {
                layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun show() {
        isShowing = true
        val translate = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, -1.0f,
            Animation.RELATIVE_TO_SELF, 0f
        )
        translate.duration = DURATION.toLong()
        val alpha = AlphaAnimation(0.0f, 1.0f)
        alpha.duration = DURATION.toLong()
        translate.setAnimationListener(this)
        contentLayout.startAnimation(translate)
        viewOutSide.startAnimation(alpha)
        visibility = View.VISIBLE
    }

    fun dismiss() {
        if (isAnimation) {
            return
        }
        if (visibility == View.GONE) {
            return
        }
        isShowing = false
        val alpha = AlphaAnimation(1.0f, 0.0f)
        val translate = TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, 0f,
            Animation.RELATIVE_TO_SELF, -1.0f
        )
        translate.duration = DURATION.toLong()
        alpha.duration = DURATION.toLong()
        alpha.setAnimationListener(this)
        contentLayout.startAnimation(translate)
        viewOutSide.startAnimation(alpha)
        if (dismissListener != null) dismissListener!!.onDismiss()
    }

    fun toggle() {
        if (visibility == View.VISIBLE) {
            dismiss()
        } else {
            show()
        }
    }

    override fun onAnimationStart(animation: Animation) {
        isAnimation = true
    }

    override fun onAnimationEnd(animation: Animation) {
        isAnimation = false
        if (animation is AlphaAnimation) {
            visibility = View.GONE
        }
    }

    override fun onAnimationRepeat(animation: Animation) {
    }

    interface OnDismissListener {
        fun onDismiss()
    }
}
