package com.waitou.wisdao_impl.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.Gravity
import com.to.aboomy.statusbar_lib.StatusBarUtil
import com.waitou.basic_lib.photo.adapter.AlbumsAdapter
import com.waitou.basic_lib.photo.viewmodule.PhotoWallViewModule
import com.waitou.wisdao_impl.R
import com.waitou.wisdao_impl.view.FolderPopUpWindow
import com.waitou.wisdao_lib.bean.Album
import com.waitou.wisdao_lib.config.WisdomConfig
import com.waitou.wisdao_lib.ui.WisdaoWallActivity
import com.waitou.wisdao_lib.ui.WisdaoWallFragment
import kotlinx.android.synthetic.main.wis_activity_photo_wall_impl.*

/**
 * auth aboom
 * date 2019-05-25
 */
class PhotoWallActivity : WisdaoWallActivity() {

    private val albumsAdapter by lazy { AlbumsAdapter() }
    private var popUpWindow: FolderPopUpWindow? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wis_activity_photo_wall_impl)
        StatusBarUtil.immersiveStatusBarNeedDark(this)
        val viewModule = ViewModelProviders.of(this)[PhotoWallViewModule::class.java]
        viewModule.albumLiveData.observe(this, Observer { addAlbum(it) })
        viewModule.selectCountLiveData.observe(this, Observer {
            preview.text = getString(R.string.wis_preview_count, it, WisdomConfig.getInstance().maxSelectLimit)
        })
        folder.setOnClickListener { showPop() }
    }

    override fun onCreateBoxingView(): WisdaoWallFragment {
        var fragment = supportFragmentManager.findFragmentByTag(PhotoWallFragment.TAG)
        if (fragment !is WisdaoWallFragment) {
            fragment = PhotoWallFragment.newInstance()
            supportFragmentManager.beginTransaction().replace(R.id.contentLayout, fragment, PhotoWallFragment.TAG)
                .commitAllowingStateLoss()
        }
        return fragment
    }

    private fun addAlbum(data: List<Album>?) {
        data?.let {
            albumsAdapter.replaceData(it)
            folder.text = it[0].albumName
            preview.text = getString(R.string.wis_preview_count, 0, WisdomConfig.getInstance().maxSelectLimit)
        }
    }

    private fun showPop() {
        if (popUpWindow == null) {
            popUpWindow = FolderPopUpWindow(this, albumsAdapter)
            albumsAdapter.function = { position ->
                if (albumsAdapter.currentAlbumPos != position) {
                    albumsAdapter.currentAlbumPos = position
                    val album = albumsAdapter.albums[position]
                    albumsAdapter.notifyDataSetChanged()
                    folder.text = album.albumName
                    val fragment = supportFragmentManager.findFragmentByTag(PhotoWallFragment.TAG)
                    if (fragment is WisdaoWallFragment) {
                        fragment.loadMedia(album.albumId)
                    }
                }
                popUpWindow?.dismiss()
            }
        }
        popUpWindow?.let {
            if (it.isShowing) {
                it.dismiss()
            }
            it.showAtLocation(footer, Gravity.BOTTOM, 0, 400)
        }
    }
}
