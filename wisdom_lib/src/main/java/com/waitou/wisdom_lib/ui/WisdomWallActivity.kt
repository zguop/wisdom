package com.waitou.wisdom_lib.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.waitou.wisdom_lib.Wisdom
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.call.OnMediaListener

/**
 * auth aboom
 * date 2019-05-24
 */
abstract class WisdomWallActivity : AppCompatActivity(), OnMediaListener {

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

    override fun nextToPreView(clazz: Class<out WisPreViewActivity>,
                               selectMedias: List<Media>,
                               currentPosition: Int,
                               albumId: String,
                               bundle: Bundle?) {
        //当前点击的position 所有选择的数据 id
        val i = Intent(this, clazz)
        i.putParcelableArrayListExtra(WisPreViewActivity.EXTRA_PREVIEW_SELECT_MEDIA, ArrayList(selectMedias))
        i.putExtra(WisPreViewActivity.EXTRA_PREVIEW_CURRENT_POSITION, currentPosition)
        i.putExtra(WisPreViewActivity.EXTRA_PREVIEW_ALBUM_ID, albumId)
        i.putExtra(WisPreViewActivity.EXTRA_PREVIEW_MODULE_TYPE, WisPreViewActivity.WIS_PREVIEW_MODULE_TYPE_EDIT)
        val fragment = supportFragmentManager.findFragmentByTag(WisdomWallFragment.TAG)
        fragment?.startActivityForResult(i, WisPreViewActivity.WIS_PREVIEW_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("aa", "activity resultCode " + resultCode + " requestCode " + requestCode)

        if (Activity.RESULT_OK != resultCode) {
            return
        }

        Log.e("aa", "activity exit  " + (requestCode - 65536) + "   - " + WisPreViewActivity.WIS_PREVIEW_REQUEST_CODE)
    }

    override fun onPreViewResult(resultMedias: List<Media>) {

    }

    override fun onResultFinish(resultMedias: List<Media>) {
        val i = Intent()
        i.putParcelableArrayListExtra(Wisdom.EXTRA_RESULT_SELECTION, ArrayList(resultMedias))
        setResult(Activity.RESULT_OK, i)
        finish()
    }
}