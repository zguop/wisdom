package com.waitou.wisdom_impl.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener
import com.to.aboomy.statusbar_lib.StatusBarUtil
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.WisdomConfig
import com.waitou.wisdom_lib.ui.WisPreViewActivity
import kotlinx.android.synthetic.main.wis_activity_preview.*

/**
 * auth aboom
 * date 2019-06-06
 */
class PhotoPreviewActivity : WisPreViewActivity(), OnOutsidePhotoTapListener {

    private lateinit var medias: List<Media>
    private var isBarHide = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wis_activity_preview)
        StatusBarUtil.transparencyBar(this, false)
        pager.addOnPageChangeListener(pageChange)
        back.setOnClickListener { onBackPressed() }
        checkView.setOnCheckedChangeListener { _, _ ->
            mediaCheckedChange(medias[currentPosition])
        }
        complete.setOnClickListener { onResultFinish(selectMedias.isNotEmpty()) }

        //不可以编辑隐藏编辑按钮
        if (!isEditor()) {
            checkView.visibility = View.GONE
            bottomBar.visibility = View.GONE
        }
    }

    override fun mediaResult(medias: List<Media>) {
        this.medias = medias
        pager.adapter = PhotoPagerAdapter(supportFragmentManager)
        pager.currentItem = currentPosition
        refreshSelectedUI(true)
    }

    private fun mediaCheckedChange(media: Media) {
        val checkedNumIndex = selectMediaIndexOf(media)
        if (checkedNumIndex > 0) {
            selectMedias.remove(media)
        } else {
            if (selectMedias.size >= WisdomConfig.getInstance().maxSelectLimit) {
                return
            }
            selectMedias.add(media)
        }
        refreshSelectedUI(false)
    }

    private fun refreshSelectedUI(initialization: Boolean) {
        val media = medias[currentPosition]
        val selectMediaIndexOf = selectMediaIndexOf(media)
        if (initialization) checkView.initCheckedNum(selectMediaIndexOf) else checkView.setCheckedNum(selectMediaIndexOf)
        complete.text = getString(R.string.wis_complete, selectMedias.size, WisdomConfig.getInstance().maxSelectLimit)
        barTitle.text = getString(R.string.wis_count, currentPosition + 1, medias.size)
    }

    private fun selectMediaIndexOf(media: Media): Int {
        val indexOf = selectMedias.indexOf(media)
        return if (indexOf >= 0) indexOf + 1 else indexOf
    }

    private inner class PhotoPagerAdapter internal constructor(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return PhotoPreviewFragment.newInstance(medias[position])
        }

        override fun getCount(): Int {
            return medias.size
        }
    }

    private val pageChange = object : ViewPager.SimpleOnPageChangeListener() {
        override fun onPageSelected(p0: Int) {
            currentPosition = p0
            refreshSelectedUI(true)
        }
    }

    override fun onOutsidePhotoTap(imageView: ImageView?) {
        val animTop = AnimationUtils.loadAnimation(this,
                if (isBarHide) R.anim.wis_top_in else R.anim.wis_top_out)
        animTop.fillAfter = true
        titleBar.startAnimation(animTop)
        checkView.isEnabled = isBarHide
        back.isEnabled = isBarHide
        //可以编辑 才有底部的动画
        if (isEditor()) {
            val animFade = AnimationUtils.loadAnimation(this,
                    if (isBarHide) R.anim.wis_fade_in else R.anim.wis_fade_out)
            bottomBar.startAnimation(animFade)
            complete.isEnabled = isBarHide
        }
        isBarHide = !isBarHide
    }
}
