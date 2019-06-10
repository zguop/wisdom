package com.waitou.wisdaoapp

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_anim.*

/**
 * auth aboom
 * date 2019-06-10
 */
class AnimActivity : AppCompatActivity(), ViewPager.OnPageChangeListener {
    override fun onPageScrollStateChanged(p0: Int) {

    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
    }

    override fun onPageSelected(p0: Int) {
    }

    private val colors = arrayListOf(Color.RED, Color.BLUE, Color.GREEN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_anim)

        pager.adapter = PhotoPagerAdapter(supportFragmentManager)
        pager.addOnPageChangeListener(this)

    }


    private inner class PhotoPagerAdapter internal constructor(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            val b = Bundle()
            b.putInt("color", colors[position])
            val fragment = AnimFragment()
            fragment.arguments = b
            return fragment
        }

        override fun getCount(): Int {
            return colors.size
        }
    }
}
