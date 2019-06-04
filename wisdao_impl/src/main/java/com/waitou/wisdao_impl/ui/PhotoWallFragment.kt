package com.waitou.wisdao_impl.ui

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.waitou.basic_lib.photo.viewmodule.PhotoWallViewModule
import com.waitou.wisdao_impl.adapter.MediasAdapter
import com.waitou.wisdao_impl.view.GridSpacingItemDecoration
import com.waitou.wisdao_lib.bean.Album
import com.waitou.wisdao_lib.bean.Media
import com.waitou.wisdao_lib.bean.ResultMedia
import com.waitou.wisdao_lib.ui.WisdaoWallFragment
import com.waitou.wisdao_lib.utils.isSingleImage

/**
 * auth aboom
 * date 2019-05-25
 */
class PhotoWallFragment : WisdaoWallFragment(), MediasAdapter.OnCheckedChangedListener {

    private lateinit var viewModule: PhotoWallViewModule
    private lateinit var adapter: MediasAdapter

    override fun albumResult(albums: List<Album>) {
        Log.e("aa", "albumResult size = " + albums.size)
        viewModule.albumLiveData.postValue(albums)
    }

    override fun mediaResult(medias: List<Media>) {
        Log.e("aa", "mediaResult size = " + medias.size)
        if (medias.isEmpty()) {
            return
        }
        adapter.replaceData(medias)
    }

    override fun startLoading() {
        loadAlbum()
        loadMedia()
    }

    companion object {
        val TAG: String = PhotoWallFragment::class.java.simpleName
        fun newInstance(): WisdaoWallFragment {
            return PhotoWallFragment()
        }
    }

    override fun checkPermissionOnDenied(permissionsDeniedForever: Array<String>, permissionsDenied: Array<String>) {
        super.checkPermissionOnDenied(permissionsDeniedForever, permissionsDenied)
        val msg = if (permissionsDeniedForever.isEmpty())
            "需要访问你的存储设备来选择图片" else "需要访问你的存储设备来选择图片，请在“系统设置”或授权对话框中允许“存储空间”权限。"
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onCameraResult(resultMedia: ResultMedia) {
        finish(arrayListOf(resultMedia))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val recyclerView = RecyclerView(activity!!)
        recyclerView.layoutParams =
            ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        recyclerView.layoutManager = GridLayoutManager(activity, 3)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(3, 4, false))
        this.adapter = MediasAdapter()
        recyclerView.adapter = adapter
        adapter.checkedListener = this
        adapter.cameraClick = View.OnClickListener { takeMedia() }
        adapter.mediaClick = View.OnClickListener {
            if (isSingleImage()) {
                finish(arrayListOf(ResultMedia((it.tag as Media).path, (it.tag as Media).uri)))
            }
        }
        return recyclerView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModule = ViewModelProviders.of(activity!!)[PhotoWallViewModule::class.java]


    }

    override fun onChange() {
        viewModule.selectCountLiveData.postValue(adapter.selectMedias.size)
    }
}
