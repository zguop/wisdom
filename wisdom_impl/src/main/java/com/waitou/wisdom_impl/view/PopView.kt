package com.waitou.wisdom_impl.view

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.*
import android.widget.FrameLayout
import com.waitou.wisdom_impl.R


class PopView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr),
    Animation.AnimationListener {

    companion object {
        private const val DURATION = 350L
        private const val DEFAULT_DIS = 0.65f
    }

    private var dismissListener: OnDismissListener? = null

    /**
     * 设置屏幕的最大占据比例
     * 屏幕的高度 * heightPercent - 140dp最小阴影
     * item最大高度超过则显示该最大比例值，否则wrap_content
     */
    private var heightPercent: Float = DEFAULT_DIS
    private var planHeight: Int = 0


    /**
     * 是否正在 展开收起 播放动画
     */
    var isAnimation: Boolean = false

    /**
     * 是否显示
     */
    var isShowing: Boolean = false

    private val popList: RecyclerView
    private val viewOutSide: View


    init {
        View.inflate(context, R.layout.wis_pop_albums, this@PopView)
        popList = findViewById(R.id.popList)
        viewOutSide = findViewById<View>(R.id.viewOutSide)
        viewOutSide.setOnClickListener {
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
     * 设置最大高度的比例 screenHeight * heightPercent
     * @param heightPercent default 0.75
     */
    fun setMaxDis(heightPercent: Float) {
        this.heightPercent = heightPercent
    }

    /**
     * 设置list最大高度，
     * 默认onMeasure中会调用，如可以知道item的高度，外部也可以直接设置高度
     *
     * @param planHeight 原本高度 itemCount * itemHeight
     * @param maxHeight 最大高度，超过最大高度，则设置高度为 maxHeight
     */
    fun setMaxItemHeight(planHeight: Int, maxHeight: Int = getMaxHeight()) {
        this.planHeight = planHeight
        val layoutParams = popList.layoutParams
        if (planHeight > maxHeight) {
            layoutParams.height = maxHeight
        } else {
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }

    fun getContentView(): RecyclerView {
        return popList
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (heightPercent != 0f && planHeight == 0) {
            val heightSpec = MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.UNSPECIFIED)
            popList.measure(widthMeasureSpec, heightSpec)
            setMaxItemHeight(popList.measuredHeight)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    fun show() {
        isShowing = true
        visibility = VISIBLE
        popList.animation = TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0f
        ).apply {
            duration = DURATION
            interpolator = DecelerateInterpolator()
            setAnimationListener(this@PopView)
        }
        viewOutSide.animation = AlphaAnimation(0.0f, 1.0f).apply {
            duration = DURATION
        }
    }

    fun dismiss() {
        if (isAnimation) {
            return
        }
        if (visibility == GONE) {
            return
        }
        isShowing = false
        popList.startAnimation(TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, 0f,
                Animation.RELATIVE_TO_SELF, -1.0f
        ).apply {
            duration = DURATION
        })
        viewOutSide.animation = AlphaAnimation(1.0f, 0.0f).apply {
            duration = DURATION
            setAnimationListener(this@PopView)
        }
        if (dismissListener != null) dismissListener!!.onDismiss()
    }

    fun toggle() {
        if (visibility == VISIBLE) {
            dismiss()
        } else {
            show()
        }
    }

    private fun getMaxHeight(): Int {
        return (resources.displayMetrics.heightPixels * heightPercent).toInt() - top
    }

    override fun onAnimationStart(animation: Animation) {
        isAnimation = true
    }

    override fun onAnimationEnd(animation: Animation) {
        isAnimation = false
        if (animation is AlphaAnimation) {
            visibility = GONE
        }
    }

    override fun onAnimationRepeat(animation: Animation) {
    }

    interface OnDismissListener {
        fun onDismiss()
    }
}
