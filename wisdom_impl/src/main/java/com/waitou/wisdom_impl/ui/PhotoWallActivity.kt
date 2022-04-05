package com.waitou.wisdom_impl.ui

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.gyf.immersionbar.ImmersionBar
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_impl.adapter.AlbumsAdapter
import com.waitou.wisdom_impl.utils.dp2pxI
import com.waitou.wisdom_impl.utils.obtainAttrRes
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

    private lateinit var barTitleTv: TextView
    private lateinit var completeTv: TextView
    private lateinit var previewTv: TextView
    private lateinit var folderPop: PopView
    private lateinit var originalCrv: CheckRadioView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wis_activity_photo_wall)

        ImmersionBar.with(this)
            .autoDarkModeEnable(true)
            .barColorInt(Color.WHITE)
            .fitsSystemWindows(true)
            .init()

        initViewsAndEvent()
        initViewModel()
        initFolderPop()
    }

    private fun initViewModel() {
        viewModule.albumLiveData.observe(this, { addAlbum(it) })
        viewModule.selectCountLiveData.observe(this, { updateBottomTextUI(it.size) })
    }

    private fun initViewsAndEvent() {
        barTitleTv = findViewById(R.id.barTitle)
        completeTv = findViewById(R.id.complete)
        previewTv = findViewById(R.id.preview)
        folderPop = findViewById(R.id.folderPop)
        originalCrv = findViewById(R.id.original)

        previewTv.setText(obtainAttrRes(R.attr.wisPreviewString, R.string.wis_preview))
        findViewById<TextView>(R.id.originalTv).setText(obtainAttrRes(R.attr.wisOriginalString, R.string.wis_original))

        barTitleTv.setOnClickListener { showPop() }
        completeTv.setOnClickListener { complete() }
        previewTv.setOnClickListener { preView() }

        val originalLayout = findViewById<View>(R.id.originalLayout)
        originalLayout.visibility = if (WisdomConfig.getInstance().hasFullImage) View.VISIBLE else View.GONE
        originalLayout.setOnClickListener { originalCrv.toggle() }
        findViewById<View>(R.id.back).setOnClickListener { onBackPressed() }
    }

    override fun onCreateFragment(tag: String): WisdomWallFragment {
        return PhotoWallFragment.newInstance().apply {
            supportFragmentManager.beginTransaction().replace(R.id.contentLayout, this, tag)
                .commitAllowingStateLoss()
        }
    }

    override fun onBackPressed() {
        if (folderPop.isShowing) folderPop.dismiss() else super.onBackPressed()
    }

    override fun isFullImage(): Boolean {
        return originalCrv.isChecked()
    }

    override fun setFullImage(fullImage: Boolean) {
        originalCrv.setChecked(fullImage)
    }

    private fun addAlbum(data: List<Album>) {
        data[0].albumName = getString(obtainAttrRes(R.attr.wisAllString, R.string.wis_all))
        barTitleTv.text = data[0].albumName
        albumsAdapter.replaceData(data)
        folderPop.setMaxItemHeight(80.dp2pxI() * data.size)
    }

    private fun updateBottomTextUI(size: Int = 0) {
        completeTv.isEnabled = size > 0
        previewTv.isEnabled = size > 0
        completeTv.text = getString(
            obtainAttrRes(R.attr.wisCompleteString, R.string.wis_complete),
            "$size/${WisdomConfig.getInstance().maxSelectLimit}")
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
                        barTitleTv.text = album.albumName
                        wisdomFragment().loadMedia(album.albumId)
                    }
                    folderPop.dismiss()
                }
            }
        }
    }

    private fun preView() {
        val value = viewModule.selectCountLiveData.value.orEmpty()
        wisdomFragment().startPreview(PhotoPreviewActivity::class.java, value, "")
    }

    private fun complete() {
        val value = viewModule.selectCountLiveData.value.orEmpty()
        if (isSingleImage() && wisdomFragment().startCrop(value[0])) {
            return
        }
        wisdomFragment().resultFinish(value)
    }
}
