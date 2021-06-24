package com.waitou.wisdom_impl.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.to.aboomy.statusbar_lib.StatusBarUtil
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_impl.adapter.AlbumsAdapter
import com.waitou.wisdom_impl.utils.tdp
import com.waitou.wisdom_impl.view.CheckRadioView
import com.waitou.wisdom_impl.view.PopView
import com.waitou.wisdom_impl.viewmodule.PhotoWallViewModule
import com.waitou.wisdom_lib.bean.Album
import com.waitou.wisdom_lib.config.WisdomConfig
import com.waitou.wisdom_lib.ui.WisdomWallActivity
import com.waitou.wisdom_lib.ui.WisdomWallFragment
import com.waitou.wisdom_lib.utils.isSingleImage

/**
 * auth aboom
 * date 2019-05-25
 */
class PhotoWallActivity : WisdomWallActivity() {

    private val albumsAdapter = AlbumsAdapter()
    private val viewModule by lazy { ViewModelProvider(this)[PhotoWallViewModule::class.java] }

    private lateinit var barTitle: TextView
    private lateinit var complete: TextView
    private lateinit var preview: View
    private lateinit var folderPop: PopView
    private lateinit var original: CheckRadioView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wis_activity_photo_wall)
        StatusBarUtil.setStatusBarColor(this, Color.WHITE)

        initViewsOrListener()
        initViewModel()
        initFolderPop()
    }

    private fun initViewModel() {
        viewModule.albumLiveData.observe(this, { addAlbum(it) })
        viewModule.selectCountLiveData.observe(this, { updateBottomTextUI(it.size) })
    }

    private fun initViewsOrListener() {
        barTitle = findViewById(R.id.barTitle)
        complete = findViewById(R.id.complete)
        preview = findViewById(R.id.preview)
        folderPop = findViewById(R.id.folderPop)
        original = findViewById(R.id.original)

        barTitle.setOnClickListener { showPop() }
        complete.setOnClickListener { complete() }
        preview.setOnClickListener { preView() }
        findViewById<View>(R.id.originalLayout).setOnClickListener { original.toggle() }
        findViewById<View>(R.id.back).setOnClickListener { onBackPressed() }
    }

    override fun onCreateFragment(tag: String): WisdomWallFragment {
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

    override fun isFullImage(): Boolean {
        return original.isChecked()
    }

    override fun setFullImage(fullImage: Boolean) {
        original.setChecked(fullImage)
    }

    private fun addAlbum(data: List<Album>) {
        data[0].albumName = getString(R.string.wis_all)
        barTitle.text = data[0].albumName
        albumsAdapter.replaceData(data)
        folderPop.setMaxItemHeight(80.tdp(this) * data.size)
    }

    private fun updateBottomTextUI(size: Int = 0) {
        complete.isEnabled = size > 0
        preview.isEnabled = size > 0
        complete.text = getString(R.string.wis_complete, size, WisdomConfig.getInstance().maxSelectLimit)
    }

    private fun showPop() {
        if (folderPop.isShowing) folderPop.dismiss() else folderPop.show()
    }

    private fun initFolderPop() {
        folderPop.getContentView().apply {
            addItemDecoration(
                DividerItemDecoration(this@PhotoWallActivity, DividerItemDecoration.VERTICAL
                ).apply {
                    ContextCompat.getDrawable(this@PhotoWallActivity, R.drawable.wis_floder_line)
                        ?.let { setDrawable(it) }
                }
            )
            layoutManager = LinearLayoutManager(this@PhotoWallActivity)
            adapter = albumsAdapter.apply {
                itemClick = { position ->
                    if (albumsAdapter.currentAlbumPos != position) {
                        albumsAdapter.currentAlbumPos = position
                        albumsAdapter.notifyDataSetChanged()
                        val album = albumsAdapter.albums[position]
                        barTitle.text = album.albumName
                        getFragment().loadMedia(album.albumId)
                    }
                    folderPop.dismiss()
                }
            }
        }
    }

    private fun preView() {
        val value = viewModule.selectCountLiveData.value
        getFragment().startPreview(PhotoPreviewActivity::class.java, value.orEmpty())
    }

    private fun complete() {
        val value = viewModule.selectCountLiveData.value.orEmpty()
        if (isSingleImage()) {
        }
        getFragment().resultFinish(value)
    }
}
