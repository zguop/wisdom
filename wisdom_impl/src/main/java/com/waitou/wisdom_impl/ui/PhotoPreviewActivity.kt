package com.waitou.wisdom_impl.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.view.animation.FastOutSlowInInterpolator
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener
import com.to.aboomy.statusbar_lib.StatusBarUtil
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_impl.view.CheckRadioView
import com.waitou.wisdom_impl.view.CheckView
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.WisdomConfig
import com.waitou.wisdom_lib.ui.WisPreViewActivity

/**
 * auth aboom
 * date 2019-06-06
 */
class PhotoPreviewActivity : WisPreViewActivity(),
    OnOutsidePhotoTapListener {

    private lateinit var medias: List<Media>
    private var isBarHide = false

    private lateinit var titleBar: View
    private lateinit var bottomBar: View
    private lateinit var pager: ViewPager
    private lateinit var checkView: CheckView
    private lateinit var complete: TextView
    private lateinit var barTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wis_activity_preview)
        StatusBarUtil.transparencyBar(this, false)

        titleBar = findViewById(R.id.titleBar)
        bottomBar = findViewById(R.id.bottomBar)
        pager = findViewById(R.id.pager)
        checkView = findViewById(R.id.checkView)
        barTitle = findViewById(R.id.barTitle)

        pager.addOnPageChangeListener(pageChange)
        findViewById<View>(R.id.back).setOnClickListener { onBackPressed() }
        checkView.setOnCheckedChangeListener { _, _ ->
            mediaCheckedChange(medias[currentPosition])
        }

        val original = findViewById<CheckRadioView>(R.id.original)
        original.setChecked(fullImage)
        findViewById<View>(R.id.originalLayout).setOnClickListener {
            fullImage = !fullImage
            original.setChecked(fullImage)
        }
        complete = findViewById(R.id.complete)
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
        if (initialization) checkView.initCheckedNum(selectMediaIndexOf) else checkView.setCheckedNum(
                selectMediaIndexOf
        )
        complete.text = getString(
                R.string.wis_complete,
                selectMedias.size,
                WisdomConfig.getInstance().maxSelectLimit
        )
        barTitle.text = getString(R.string.wis_count, currentPosition + 1, medias.size)
    }

    private fun selectMediaIndexOf(media: Media): Int {
        val indexOf = selectMedias.indexOf(media)
        return if (indexOf >= 0) indexOf + 1 else indexOf
    }

    private inner class PhotoPagerAdapter(fm: FragmentManager) :
        FragmentStatePagerAdapter(fm) {

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
        if (isBarHide) {
            titleBar.animate()
                .setInterpolator(FastOutSlowInInterpolator())
                .translationYBy(titleBar.height.toFloat())
                .start()
            if (isEditor()) {
                bottomBar.animate()
                    .setInterpolator(FastOutSlowInInterpolator())
                    .translationYBy(-bottomBar.height.toFloat())
                    .start()
            }
        } else {
            titleBar.animate()
                .setInterpolator(FastOutSlowInInterpolator())
                .translationYBy(-titleBar.height.toFloat())
                .start()
            if (isEditor()) {
                bottomBar.animate()
                    .setInterpolator(FastOutSlowInInterpolator())
                    .translationYBy(bottomBar.height.toFloat())
                    .start()
            }
        }
        isBarHide = !isBarHide
    }
}
