package com.waitou.wisdom_impl.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_impl.adapter.MediasAdapter
import com.waitou.wisdom_impl.utils.launchAppDetailsSettings
import com.waitou.wisdom_impl.view.GridSpacingItemDecoration
import com.waitou.wisdom_impl.viewmodule.PhotoWallViewModule
import com.waitou.wisdom_lib.bean.Album
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.WisdomConfig
import com.waitou.wisdom_lib.ui.WisdomWallFragment
import com.waitou.wisdom_lib.utils.isSingleImage
import com.waitou.wisdom_lib.utils.onlyImages
import com.waitou.wisdom_lib.utils.onlyVideos
import kotlin.math.log

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
    private var toStorageSetting = false

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
            addItemDecoration(GridSpacingItemDecoration(3, 1, false))
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
                    //没有相机的时候position的值是正确的，预览页面不存在相机的位置，有相机需要-1才对
                    startPreview(
                        PhotoPreviewActivity::class.java,
                        selectMedias,
                        currentPosition = if (WisdomConfig.getInstance().isCamera) position - 1 else position,
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (toStorageSetting) {
            toStorageSetting = false
            requestStartPermissionLaunch()
        }
    }

    override fun onCameraPermissionDenied(isDeniedForever: Boolean) {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.wis_camera_permission_denied)
            .setPositiveButton(if (isDeniedForever) R.string.wis_to_setting else R.string.wis_awarded) { _, _ ->
                if (isDeniedForever) {
                    launchAppDetailsSettings(requireContext())
                } else {
                    requestCameraPermissionLaunch()
                }
            }
            .setNegativeButton(R.string.wis_cancel, null)
            .create()
            .show()
    }

    override fun onStoragePermissionDenied(isDeniedForever: Boolean) {
        AlertDialog.Builder(requireContext())
            .setMessage(R.string.wis_storage_permission_denied)
            .setPositiveButton(if (isDeniedForever) R.string.wis_to_setting else R.string.wis_awarded) { _, _ ->
                if (isDeniedForever) {
                    toStorageSetting = true
                    launchAppDetailsSettings(requireContext())
                } else {
                    requestStartPermissionLaunch()
                }
            }
            .setNegativeButton(R.string.wis_cancel) { _, _ ->
                requireActivity().finish()
            }
            .setCancelable(false)
            .create()
            .show()
    }

    override fun onCameraResult(media: Media) {
        if (currentAlbumId == Album.ALBUM_ID_ALL) {
            if (adapter.medias.find { media == it } == null) {
                adapter.medias.add(1, media)
                adapter.notifyItemInserted(1)
            }
            adapter.mediaCheckedChange(media, 1)
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

    override fun onPreviewResult(medias: List<Media>, exit: Boolean) {
        if (exit) {
            if (isSingleImage() && startCrop(medias[0])) {
                return
            }
            resultFinish(medias)
        } else {
            adapter.replaceSelectMedias(medias)
            viewModule.selectCountLiveData.value = adapter.selectMedias
        }
    }
}
