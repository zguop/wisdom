package com.waitou.wisdom_impl.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Checkable
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_impl.utils.dp2pxF
import kotlin.math.max

/**
 * auth aboom
 * date 2019-06-02
 */
@SuppressLint("CustomViewStyleable")
class CheckView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr),
    Checkable {

    private val strokePaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    }

    private val bgPaint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
    }

    private val textPaint = TextPaint().apply {
        isAntiAlias = true
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private var shadowPaint: Paint? = null

    private var checkNum = UNCHECKED
    private var isChecked: Boolean = false
    private var size: Float = 0f
    private var shadowWidth: Float = 0f
    private var listener: OnCheckedChangeListener? = null

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.wis_CheckView)
        val ta = context.theme.obtainStyledAttributes(intArrayOf(R.attr.colorPrimary))
        size = a.getDimension(R.styleable.wis_CheckView_wis_size, 48.dp2pxF())
        shadowWidth = a.getDimension(R.styleable.wis_CheckView_wis_shadow_width, 6.dp2pxF())
        bgPaint.color = a.getColor(R.styleable.wis_CheckView_wis_check_color, ta.getColor(0, Color.parseColor("#FB4846")))
        strokePaint.color = a.getColor(R.styleable.wis_CheckView_wis_stroke_color, Color.WHITE)
        strokePaint.strokeWidth = a.getDimension(R.styleable.wis_CheckView_wis_stroke_width, 3.dp2pxF())
        textPaint.textSize = a.getDimension(R.styleable.wis_CheckView_wis_check_text_size, 14.dp2pxF())
        textPaint.color = a.getColor(R.styleable.wis_CheckView_wis_check_text_color, Color.WHITE)
        a.recycle()
        ta.recycle()
        setOnClickListener { toggle() }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val sizeSpec = MeasureSpec.makeMeasureSpec(size.toInt(), MeasureSpec.EXACTLY)
        super.onMeasure(sizeSpec, sizeSpec)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val center = size / 2
        val padding = max(max(paddingLeft, paddingRight), max(paddingTop, paddingBottom))
        val outerRadius = center - strokePaint.strokeWidth / 2 - padding
        val innerRadius = center - strokePaint.strokeWidth - padding
        val gradientRadius = outerRadius + shadowWidth

        if (shadowPaint == null) {
            val stop0 = (innerRadius - strokePaint.strokeWidth - padding) / gradientRadius
            val stop1 = innerRadius / gradientRadius
            val stop2 = outerRadius / gradientRadius
            val stop3 = 1.0f
            shadowPaint = Paint().apply {
                isAntiAlias = true
                shader = RadialGradient(
                    center,
                    center,
                    gradientRadius,
                    intArrayOf(Color.parseColor("#00000000"), Color.parseColor("#0D000000"),
                        Color.parseColor("#0D000000"), Color.parseColor("#00000000")),
                    floatArrayOf(stop0, stop1, stop2, stop3),
                    Shader.TileMode.CLAMP
                )
            }
        }
        canvas.drawCircle(center, center, gradientRadius, shadowPaint!!)
        canvas.drawCircle(center, center, outerRadius, strokePaint)
        if (isChecked && checkNum > UNCHECKED) {
            canvas.drawCircle(center, center, innerRadius, bgPaint)
            val text = checkNum.toString()
            val baseX = (width - textPaint.measureText(text)) / 2
            val baseY = (height - textPaint.descent() - textPaint.ascent()) / 2
            canvas.drawText(text, baseX, baseY, textPaint)
        }
        alpha = if (isEnabled) 1.0f else 0.5f
    }

    override fun toggle() {
        setChecked(!isChecked)
    }

    /**
     * 只有当两者都为true 才算checked
     */
    override fun isChecked(): Boolean {
        return isChecked && checkNum > UNCHECKED
    }

    override fun setChecked(checked: Boolean) {
        if (isChecked != checked) {
            isChecked = checked
            listener?.onCheckedChanged(this, checked)
        }
    }

    fun setCheckedNum(checkedNum: Int) {
        //已经勾选了，checkNum = UNCHECKED 表示第一次勾选，开启动画
        if (checkedNum > UNCHECKED && checkNum == UNCHECKED) {
            val loadAnimation = AnimationUtils.loadAnimation(context, R.anim.wis_check_in)
            startAnimation(loadAnimation)
        }
        initCheckedNum(checkedNum)
    }

    /**
     * 初始化数字checked，不会触发onCheckedChanged
     */
    fun initCheckedNum(checkedNum: Int) {
        this.checkNum = checkedNum
        this.isChecked = checkNum > UNCHECKED
        invalidate()
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(checkView: CheckView, isChecked: Boolean)
    }

    fun setOnCheckedChangeListener(l: OnCheckedChangeListener.(checkView: CheckView, isChecked: Boolean) -> Unit) {
        listener = object : OnCheckedChangeListener {
            override fun onCheckedChanged(checkView: CheckView, isChecked: Boolean) {
                l.invoke(this, checkView, isChecked)
            }
        }
    }

    companion object {
        const val UNCHECKED = -1
    }
}
