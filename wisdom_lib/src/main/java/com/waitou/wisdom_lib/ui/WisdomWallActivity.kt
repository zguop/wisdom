package com.waitou.wisdom_lib.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.waitou.wisdom_lib.interfaces.IFullImage

/**
 * auth aboom
 * date 2019-05-24
 */
abstract class WisdomWallActivity : AppCompatActivity(),
    IFullImage {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateFragment(WisdomWallFragment.TAG)
    }

    abstract fun onCreateFragment(tag: String): WisdomWallFragment

    fun getFragment(): WisdomWallFragment {
        return supportFragmentManager.findFragmentByTag(WisdomWallFragment.TAG) as WisdomWallFragment
    }
}