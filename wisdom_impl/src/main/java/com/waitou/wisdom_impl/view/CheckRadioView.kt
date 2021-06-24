package com.waitou.wisdom_impl.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PorterDuff
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.waitou.wisdom_impl.R

@SuppressLint("CustomViewStyleable", "ResourceType")
class CheckRadioView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {
    private val selectedColor: Int
    private val unColor: Int

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.wis_CheckRadioView)
        val ta = context.theme.obtainStyledAttributes(
            intArrayOf(
                R.attr.wisCheckRadioSelectorColor,
                R.attr.wisCheckRadioNormalColor
            )
        )
        selectedColor = a.getColor(
            R.styleable.wis_CheckRadioView_wis_selected_color,
            ta.getColor(0, Color.parseColor("#808080"))
        )
        unColor =
            a.getColor(
                R.styleable.wis_CheckRadioView_wis_normal_color,
                ta.getColor(1, Color.parseColor("#808080"))
            )
        a.recycle()
        ta.recycle()

        setChecked(false)
    }

    fun toggle() {
        setChecked(!isSelected)
    }

    fun isChecked(): Boolean {
        return isSelected
    }

    fun setChecked(enable: Boolean) {
        if (enable) {
            isSelected = true
            setImageResource(R.drawable.wis_svg_ic_radio_selected_color)
            drawable.setColorFilter(selectedColor, PorterDuff.Mode.SRC_IN)
        } else {
            isSelected = false
            setImageResource(R.drawable.wis_svg_ic_radio_normal_color)
            drawable.setColorFilter(unColor, PorterDuff.Mode.SRC_IN)
        }
    }
}