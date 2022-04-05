package com.waitou.wisdom_impl.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.viewpager.widget.ViewPager
import com.gyf.immersionbar.ImmersionBar
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_impl.utils.obtainAttrRes
import com.waitou.wisdom_impl.view.CheckRadioView
import com.waitou.wisdom_impl.view.CheckView
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.WisdomConfig
import com.waitou.wisdom_lib.ui.WisPreViewActivity

/**
 * auth aboom
 * date 2019-06-06
 */
class PhotoPreviewActivity : WisPreViewActivity() {

    private lateinit var medias: List<Media>
    private var isBarHide = false

    private lateinit var titleBar: View
    private lateinit var bottomBar: View
    private lateinit var pager: ViewPager
    private lateinit var checkView: CheckView
    private lateinit var completeTv: TextView
    private lateinit var barTitleTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wis_activity_preview)

        ImmersionBar.with(this)
            .autoDarkModeEnable(true)
            .transparentStatusBar()
            .init()

        initViewsAndEvent()

        //不可以编辑隐藏编辑按钮
        if (!isEditor()) {
            checkView.visibility = View.GONE
            bottomBar.visibility = View.GONE
        }
    }

    private fun initViewsAndEvent() {
        titleBar = findViewById(R.id.titleBar)
        bottomBar = findViewById(R.id.bottomBar)
        pager = findViewById(R.id.pager)
        checkView = findViewById(R.id.checkView)
        barTitleTv = findViewById(R.id.barTitle)
        completeTv = findViewById(R.id.complete)

        completeTv.setOnClickListener { onResultFinish(selectMedias.isNotEmpty()) }

        findViewById<TextView>(R.id.originalTv)
            .setText(obtainAttrRes(R.attr.wisOriginalString, R.string.wis_original))

        val original = findViewById<CheckRadioView>(R.id.original)
        original.setChecked(fullImage)

        val originalLayout = findViewById<View>(R.id.originalLayout)
        originalLayout.visibility = if (WisdomConfig.getInstance().hasFullImage) View.VISIBLE else View.GONE
        originalLayout.setOnClickListener {
            fullImage = !fullImage
            original.setChecked(fullImage)
        }
        findViewById<View>(R.id.back).setOnClickListener { onBackPressed() }
        pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(p0: Int) {
                currentPosition = p0
                refreshSelectedUI(true)
            }
        })
        checkView.setOnCheckedChangeListener { _, _ ->
            mediaCheckedChange(medias[currentPosition])
        }
    }

    override fun mediaResult(medias: List<Media>) {
        this.medias = medias
        pager.adapter = PhotoPagerAdapter(supportFragmentManager)
        pager.setCurrentItem(currentPosition, false)
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

    @SuppressLint("SetTextI18n")
    private fun refreshSelectedUI(initialization: Boolean) {
        val media = medias[currentPosition]
        val selectMediaIndexOf = selectMediaIndexOf(media)
        if (initialization) checkView.initCheckedNum(selectMediaIndexOf) else checkView.setCheckedNum(selectMediaIndexOf)
        checkView.isEnabled = !(selectMedias.size >= WisdomConfig.getInstance().maxSelectLimit && !checkView.isChecked)
        completeTv.text = getString(
            obtainAttrRes(R.attr.wisCompleteString, R.string.wis_complete),
            "${selectMedias.size}/${WisdomConfig.getInstance().maxSelectLimit}"
        )
        barTitleTv.text = "${currentPosition + 1}/${medias.size}"
    }

    private fun selectMediaIndexOf(media: Media): Int {
        val indexOf = selectMedias.indexOf(media)
        return if (indexOf >= 0) indexOf + 1 else indexOf
    }

    private inner class PhotoPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return PhotoPreviewFragment.newInstance(medias[position])
        }

        override fun getCount(): Int {
            return medias.size
        }
    }

    fun onOutsidePhotoTap() {
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
