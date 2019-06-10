package com.waitou.wisdom_impl.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.PopupWindow
import com.waitou.wisdom_impl.R
import kotlinx.android.synthetic.main.wis_pop_albums.view.*

/**
 * auth aboom
 * date 2019-05-25
 */
class FolderPopWindow(context: Context, adapter: RecyclerView.Adapter<*>) : PopupWindow(context) {

    init {
        contentView = View.inflate(context, R.layout.wis_pop_albums, null)
        contentView.popList.layoutManager = LinearLayoutManager(context)
        contentView.popList.adapter = adapter
        animationStyle = R.style.wis_pop_style_fade
        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
        setBackgroundDrawable(ColorDrawable(Color.argb(80, 0, 0, 0)))
        contentView.setOnClickListener { dismiss() }
    }

    override fun showAsDropDown(parent: View) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val rect = Rect()
                parent.getGlobalVisibleRect(rect)
                val h = parent.resources.displayMetrics.heightPixels - rect.bottom
                height = h
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.showAsDropDown(parent)
        contentView.popList.post {
            val maxHeight = (Resources.getSystem().displayMetrics.density * 300 + .5f).toInt()
            val height = contentView.popList.height
            if (height > maxHeight) {
                val layoutParams = contentView.popList.layoutParams
                layoutParams.height = maxHeight
                contentView.popList.layoutParams = layoutParams
            }
            val showAnim = AnimationUtils.loadAnimation(contentView.context, R.anim.wis_album_show)
            contentView.popList.startAnimation(showAnim)
        }
    }

    override fun dismiss() {
        val dismissAnim = AnimationUtils.loadAnimation(contentView.context, R.anim.wis_album_dismiss)
        contentView.popList.startAnimation(dismissAnim)
        dismissAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                super@FolderPopWindow.dismiss()
            }
        })
    }
}
