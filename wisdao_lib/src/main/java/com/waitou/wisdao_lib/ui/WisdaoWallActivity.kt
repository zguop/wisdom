package com.waitou.wisdao_lib.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.waitou.wisdao_lib.call.OnResultMediaListener

/**
 * auth aboom
 * date 2019-05-24
 */
abstract class WisdaoWallActivity : AppCompatActivity(), OnResultMediaListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment = onCreateBoxingView()
        fragment.onResultMediaListener = this
    }

    abstract fun onCreateBoxingView(): WisdaoWallFragment

    override fun onResultFinish(data: Intent) {
        setResult(Activity.RESULT_OK, data)
        finish()
    }
}