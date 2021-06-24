package com.waitou.wisdom_impl.ui

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
class PhotoWallFragment : WisdomWallFragment() {

    companion object {
        fun newInstance(): WisdomWallFragment {
            return PhotoWallFragment()
        }
    }

    private val viewModule by lazy { ViewModelProvider(requireActivity())[PhotoWallViewModule::class.java] }
    private val adapter = MediasAdapter()

    override fun albumResult(albums: List<Album>) {
        viewModule.albumLiveData.value = albums
        viewModule.selectCountLiveData.value = adapter.selectMedias
    }

    override fun mediaResult(medias: List<Media>) {
        adapter.replaceMedias(medias)
    }

    override fun startLoading() {
        loadAlbum()
        loadMedia()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return RecyclerView(requireActivity()).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            layoutManager = GridLayoutManager(activity, 3)
            addItemDecoration(GridSpacingItemDecoration(3, 2, false))
            adapter = this@PhotoWallFragment.adapter.apply {
                checkedListener = {
                    viewModule.selectCountLiveData.value = this@PhotoWallFragment.adapter.selectMedias
                }
                cameraClick = View.OnClickListener {
                    when {
                        onlyImages() -> startCameraImage()
                        onlyVideos() -> startCameraVideo()
                        else -> startCameraImage()
                    }
                }
                mediaClick = { position ->
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

    override fun onCheckPermissionResult(permissionsDeniedForever: Array<String>, permissionsDenied: Array<String>) {
        val msg = if (Manifest.permission.READ_EXTERNAL_STORAGE == permissionsDenied[0]) "需要访问设备的存储权限来选择图片" else "需要访问设备的相机权限进行拍照或录像"
        Toast.makeText(
            activity,
            if (permissionsDeniedForever.isNotEmpty()) "$msg，请在“系统设置”或授权对话框中允许“存储空间”权限。" else msg,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onCameraResult(media: Media) {
        if (!startCrop(media)) {
            resultFinish(listOf(media))
        }
    }

    override fun onCropResult(media: Media) {
        resultFinish(listOf(media))
    }

    override fun beforeSelectorMedias(imgMedias: List<Media>?) {
        if (!imgMedias.isNullOrEmpty()) {
            if (isSingleImage()) { //单选只取其中一个
                adapter.selectMedias.add(imgMedias[0])
            } else {
                adapter.selectMedias.addAll(imgMedias)
            }
        }
    }

    override fun onPreviewResult(medias: List<Media>) {
        adapter.replaceSelectMedias(medias)
        viewModule.selectCountLiveData.value = adapter.selectMedias
    }
}
