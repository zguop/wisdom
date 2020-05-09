package com.waitou.wisdom_lib.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.waitou.wisdom_lib.Wisdom
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.call.OnMediaListener
import com.waitou.wisdom_lib.config.WisdomConfig

/**
 * auth aboom
 * date 2019-05-24
 */
abstract class WisdomWallActivity : AppCompatActivity(), OnMediaListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fragment = onCreateView(WisdomWallFragment.TAG)
        fragment.onResultMediaListener = this
    }

    abstract fun onCreateView(tag: String): WisdomWallFragment

    fun loadMedia(albumId: String) {
        val fragment = supportFragmentManager.findFragmentByTag(WisdomWallFragment.TAG)
        if (fragment is WisdomWallFragment) {
            fragment.loadMedia(albumId)
        }
    }

    override fun startPreview(clazz: Class<out WisPreViewActivity>, selectMedias: List<Media>, currentPosition: Int, albumId: String, bundle: Bundle?) {
        //当前点击的position 所有选择的数据 mediaId
        val i = WisPreViewActivity.getIntent(
            this,
            clazz,
            selectMedias,
            currentPosition,
            albumId,
            WisPreViewActivity.WIS_PREVIEW_MODULE_TYPE_EDIT
        )
        val fragment = supportFragmentManager.findFragmentByTag(WisdomWallFragment.TAG)
        fragment?.startActivityForResult(i, WisPreViewActivity.WIS_PREVIEW_REQUEST_CODE, bundle)
    }

    override fun onPreViewResult(resultMedias: List<Media>) {

    }

    override fun onResultFinish(resultMedias: List<Media>) {
        compress(resultMedias) {
            val i = Intent()
            i.putParcelableArrayListExtra(Wisdom.EXTRA_RESULT_SELECTION, ArrayList(resultMedias))
            setResult(Activity.RESULT_OK, i)
            finish()
        }
    }

    private fun compress(resultMedias: List<Media>, function: () -> Unit) {
        val engine = WisdomConfig.getInstance().compressEngine
        engine ?: function.invoke()
        engine?.compress(this, resultMedias, function)
    }
}