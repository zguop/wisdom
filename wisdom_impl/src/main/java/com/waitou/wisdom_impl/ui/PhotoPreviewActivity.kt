package com.waitou.wisdom_impl.ui

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import com.to.aboomy.statusbar_lib.StatusBarUtil
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.WisdomConfig
import com.waitou.wisdom_lib.ui.WisPreViewActivity
import kotlinx.android.synthetic.main.wis_activity_preview.*
import kotlinx.android.synthetic.main.wis_include_title_bar.*

/**
 * auth aboom
 * date 2019-06-06
 */
class PhotoPreviewActivity : WisPreViewActivity() {

    private lateinit var medias: List<Media>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wis_activity_preview)
        StatusBarUtil.transparencyBar(this, false)
        titleBar.setBackgroundColor(Color.TRANSPARENT)
        pager.addOnPageChangeListener(pageChange)
        back.setOnClickListener { onBackPressed() }
        checkView.setOnCheckedChangeListener { _, _ ->
            mediaCheckedChange(medias[currentPosition])
        }
        complete.setOnClickListener { onResultFinish(selectMedias.isNotEmpty()) }
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
}
