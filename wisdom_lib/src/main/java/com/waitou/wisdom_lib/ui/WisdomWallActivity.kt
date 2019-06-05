package com.waitou.wisdom_lib.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.waitou.wisdom_lib.Wisdom
import com.waitou.wisdom_lib.bean.ResultMedia
import com.waitou.wisdom_lib.call.OnResultMediaListener

/**
 * auth aboom
 * date 2019-05-24
 */
abstract class WisdomWallActivity : AppCompatActivity(), OnResultMediaListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment = onCreateBoxingView(WisdomWallFragment.TAG)
        fragment.onResultMediaListener = this
    }

    abstract fun onCreateBoxingView(tag: String): WisdomWallFragment

    fun loadMedia(albumId: String) {
        val fragment = supportFragmentManager.findFragmentByTag(WisdomWallFragment.TAG)
        if (fragment is WisdomWallFragment) {
            fragment.loadMedia(albumId)
        }
    }

    override fun onResultFinish(resultMedias: ArrayList<ResultMedia>) {
        val i = Intent()
        i.putParcelableArrayListExtra(Wisdom.EXTRA_RESULT_SELECTION, resultMedias)
        setResult(Activity.RESULT_OK, i)
        finish()
    }
}