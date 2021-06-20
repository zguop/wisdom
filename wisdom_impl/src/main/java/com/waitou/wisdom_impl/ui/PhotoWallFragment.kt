package com.waitou.wisdom_impl.ui

import android.Manifest
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.waitou.wisdom_impl.adapter.MediasAdapter
import com.waitou.wisdom_impl.view.GridSpacingItemDecoration
import com.waitou.wisdom_impl.viewmodule.PhotoWallViewModule
import com.waitou.wisdom_lib.bean.Album
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.WisdomConfig
import com.waitou.wisdom_lib.ui.WisdomWallFragment
import com.waitou.wisdom_lib.utils.isSingleImage
import com.waitou.wisdom_lib.utils.onlyImages
import com.waitou.wisdom_lib.utils.onlyVideos

/**
 * auth aboom
 * date 2019-05-25
 */
class PhotoWallFragment : WisdomWallFragment(),
    MediasAdapter.OnCheckedChangedListener {

    companion object {
        fun newInstance(): WisdomWallFragment {
            return PhotoWallFragment()
        }
    }

    private val viewModule by lazy { ViewModelProviders.of(requireActivity())[PhotoWallViewModule::class.java] }
    private val adapter = MediasAdapter()

    override fun albumResult(albums: List<Album>) {
        viewModule.albumLiveData.postValue(albums)
        onChange()
    }

    override fun mediaResult(medias: List<Media>) {
        if (medias.isEmpty()) {
            return
        }
        adapter.replaceMedias(medias)
    }

    override fun startLoading() {
        loadAlbum()
        loadMedia()
    }

    override fun checkPermissionOnDenied(permissionsDeniedForever: Array<String>, permissionsDenied: Array<String>) {
        val msg = if (Manifest.permission.READ_EXTERNAL_STORAGE == permissionsDenied[0]) "需要访问设备的存储权限来选择图片" else "需要访问设备的相机权限进行拍照或录像"
        Toast.makeText(
                activity,
                if (permissionsDeniedForever.isNotEmpty()) "$msg，请在“系统设置”或授权对话框中允许“存储空间”权限。" else msg,
                Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCameraResult(media: Media) {
        if (!startCrop(media)) {
            finish(listOf(media))
        }
    }

    override fun onCropResult(media: Media) {
        finish(listOf(media))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return RecyclerView(requireActivity()).apply {
            layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            )
            layoutManager = GridLayoutManager(activity, 3)
            addItemDecoration(GridSpacingItemDecoration(3, 4, false))
            adapter = this@PhotoWallFragment.adapter.apply {
                checkedListener = this@PhotoWallFragment
                cameraClick = View.OnClickListener {
                    when {
                        onlyImages() -> startCameraImage()
                        onlyVideos() -> startCameraVideo()
                        else -> startCameraImage()
                    }
                }
                mediaClick = { media, position, _ ->
                    if (isSingleImage()) {
                        //单选完成结束
                        if (!startCrop(media)) {
                            finish(listOf(media))
                        }
                    } else {
                        //val make = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, view, "preview")
                        //没有相机的时候position的值是正确的，预览页面不存在相机的位置，有相机需要-1才对
                        startPreview(
                            PhotoPreviewActivity::class.java,
                            selectMedias,
                            if (WisdomConfig.getInstance().isCamera) position - 1 else position,
                            null
                        )
                    }
                }
            }
        }
    }

    override fun onChange() {
        viewModule.selectCountLiveData.postValue(adapter.selectMedias)
    }

    override fun beforeSelectorMedias(imgMedias: List<Media>?) {
        if (!isSingleImage()) {
            imgMedias?.let {
                adapter.selectMedias.addAll(it)
            }
        }
    }

    override fun onPreviewResult(medias: List<Media>) {
        adapter.replaceSelectMedias(medias)
        onChange()
    }
}
