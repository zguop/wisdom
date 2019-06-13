package com.waitou.wisdom_impl.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import com.to.aboomy.statusbar_lib.StatusBarUtil
import com.waitou.basic_lib.photo.adapter.AlbumsAdapter
import com.waitou.basic_lib.photo.viewmodule.PhotoWallViewModule
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_impl.view.FolderPopWindow
import com.waitou.wisdom_lib.bean.Album
import com.waitou.wisdom_lib.config.WisdomConfig
import com.waitou.wisdom_lib.ui.WisdomWallActivity
import com.waitou.wisdom_lib.ui.WisdomWallFragment
import kotlinx.android.synthetic.main.wis_activity_photo_wall.*
import kotlinx.android.synthetic.main.wis_include_title_bar.*

/**
 * auth aboom
 * date 2019-05-25
 */
class PhotoWallActivity : WisdomWallActivity() {

    private val albumsAdapter by lazy { AlbumsAdapter() }
    private var popUpWindow: FolderPopWindow? = null
    private lateinit var viewModule: PhotoWallViewModule

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wis_activity_photo_wall)
        StatusBarUtil.setStatusBarColor(this, Color.WHITE)
        viewModule = ViewModelProviders.of(this)[PhotoWallViewModule::class.java]
        viewModule.albumLiveData.observe(this, Observer { addAlbum(it) })
        viewModule.selectCountLiveData.observe(this, Observer { data ->
            data?.let {
                updateBottomTextUI(it.size)
            }
        })
        barTitle.setOnClickListener { showPop() }
        complete.setOnClickListener { complete() }
        preview.setOnClickListener { preView() }
        back.setOnClickListener { onBackPressed() }
    }

    override fun onCreateBoxingView(tag: String): WisdomWallFragment {
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment !is WisdomWallFragment) {
            fragment = PhotoWallFragment.newInstance()
            supportFragmentManager.beginTransaction().replace(R.id.contentLayout, fragment, tag)
                    .commitAllowingStateLoss()
        }
        return fragment
    }

    override fun onBackPressed() {
        if (popUpWindow?.isShowing == true) popUpWindow?.dismiss() else super.onBackPressed()
    }

    private fun addAlbum(data: List<Album>?) {
        data?.let {
            albumsAdapter.replaceData(it)
            barTitle.text = it[0].albumName
            updateBottomTextUI()
        }
    }

    private fun updateBottomTextUI(size: Int = 0) {
        complete.isEnabled = size > 0
        preview.isEnabled = size > 0
        complete.text = getString(R.string.wis_complete, size, WisdomConfig.getInstance().maxSelectLimit)
    }

    private fun showPop() {
        if (popUpWindow == null) {
            popUpWindow = FolderPopWindow(this, albumsAdapter)
            albumsAdapter.function = { position ->
                if (albumsAdapter.currentAlbumPos != position) {
                    albumsAdapter.currentAlbumPos = position
                    val album = albumsAdapter.albums[position]
                    albumsAdapter.notifyDataSetChanged()
                    barTitle.text = album.albumName
                    loadMedia(album.albumId)
                }
                popUpWindow?.dismiss()
            }
        }
        popUpWindow?.apply {
            if (isShowing) dismiss() else showAsDropDown(barTitle)
        }
    }

    private fun preView() {
        val value = viewModule.selectCountLiveData.value
        nextToPreView(PhotoPreviewActivity::class.java, value.orEmpty())
    }

    private fun complete() {
        val value = viewModule.selectCountLiveData.value
        onResultFinish(value.orEmpty())
    }
}
