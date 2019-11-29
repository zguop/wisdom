package com.waitou.wisdom_impl.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.to.aboomy.statusbar_lib.StatusBarUtil
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_impl.adapter.AlbumsAdapter
import com.waitou.wisdom_impl.viewmodule.PhotoWallViewModule
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

    override fun onCreateView(tag: String): WisdomWallFragment {
        var fragment = supportFragmentManager.findFragmentByTag(tag)
        if (fragment !is WisdomWallFragment) {
            fragment = PhotoWallFragment.newInstance()
            supportFragmentManager.beginTransaction().replace(R.id.contentLayout, fragment, tag)
                .commitAllowingStateLoss()
        }
        return fragment
    }

    override fun onBackPressed() {
        if (folderPop.isShowing) folderPop.dismiss() else super.onBackPressed()
    }

    private fun addAlbum(data: List<Album>?) {
        data?.let {
            it[0].albumName = getString(R.string.wis_all)
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
        folderPop.getContentView().adapter?:let {
            folderPop.getContentView().layoutManager = LinearLayoutManager(this)
            folderPop.getContentView().adapter = albumsAdapter
            albumsAdapter.function = { position ->
                if (albumsAdapter.currentAlbumPos != position) {
                    albumsAdapter.currentAlbumPos = position
                    val album = albumsAdapter.albums[position]
                    albumsAdapter.notifyDataSetChanged()
                    barTitle.text = album.albumName
                    loadMedia(album.albumId)
                }
               folderPop.dismiss()
            }
        }
        if(folderPop.isShowing) folderPop.dismiss() else folderPop.show()
    }

    private fun preView() {
        val value = viewModule.selectCountLiveData.value
        startPreview(PhotoPreviewActivity::class.java, value.orEmpty())
    }

    private fun complete() {
        val value = viewModule.selectCountLiveData.value
        onResultFinish(value.orEmpty())
    }
}
