package com.waitou.wisdom_lib.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import com.waitou.wisdom_lib.bean.Album
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.call.ILoaderAlbumCall
import com.waitou.wisdom_lib.call.ILoaderMediaCall
import com.waitou.wisdom_lib.call.OnMediaListener
import com.waitou.wisdom_lib.config.WisdomConfig
import com.waitou.wisdom_lib.loader.AlbumCollection
import com.waitou.wisdom_lib.loader.MediaCollection
import com.waitou.wisdom_lib.utils.CameraStrategy

/**
 * auth aboom
 * date 2019-05-24
 */
abstract class WisdomWallFragment : Fragment(), ILoaderAlbumCall, ILoaderMediaCall {

    companion object {
        @JvmField
        val TAG: String = WisdomWallFragment::class.java.name + hashCode()
    }

    var onResultMediaListener: OnMediaListener? = null
    lateinit var currentAlbumId: String

    private val albumCollection by lazy { AlbumCollection() }
    private val mediaCollection by lazy { MediaCollection() }
    private val cameraStrategy by lazy { CameraStrategy(this) }

    private var cameraPermissionGranted: (() -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            albumCollection.onCreate(it, this)
            mediaCollection.onCreate(it, this)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        checkPermissionOnStart()
    }

    private fun checkPermissionOnStart() {
        activity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(it, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            } else {
                startLoading()
            }
        }
    }

    private fun checkPermissionOnCamera(cameraPermissionGranted: (() -> Unit)?) {
        activity?.let {
            if (ActivityCompat.checkSelfPermission(it, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                this.cameraPermissionGranted = cameraPermissionGranted
                requestPermissions(arrayOf(Manifest.permission.CAMERA), PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
            } else {
                cameraPermissionGranted?.invoke()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (permissions.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    startLoading()
                }
                if (permissions.contains(Manifest.permission.CAMERA)) {
                    checkPermissionOnCamera(cameraPermissionGranted)
                }
            } else {
                if (shouldShowRequestPermissionRationale(permissions[0])) {
                    checkPermissionOnDenied(emptyArray(), permissions)
                } else {
                    checkPermissionOnDenied(permissions, permissions)
                }
            }
        }
    }

    /**
     * 加载目录
     */
    fun loadAlbum() {
        albumCollection.loadAlbum()
    }

    /**
     * 加载相册
     * @param albumId 目录id 默认加载全部
     */
    fun loadMedia(albumId: String = Album.ALBUM_ID_ALL) {
        currentAlbumId = albumId
        mediaCollection.loadMedia(albumId)
    }

    /**
     * 打开相机拍照 复写onCameraResult方法接收相机回调
     */
    fun startCameraImage() {
        checkPermissionOnCamera {
            activity?.let {
                cameraStrategy.startCamera(it, WisdomConfig.getInstance().authorities, WisdomConfig.getInstance().directory)
            }
        }
    }

    /**
     * 打开相机录像，复写onCameraResult方法接收相机回调
     */
    fun startCameraVideo() {
        checkPermissionOnCamera {
            activity?.let {
                cameraStrategy.startCameraVideo(it, WisdomConfig.getInstance().authorities, WisdomConfig.getInstance().directory)
            }
        }
    }

    /**
     * 关闭 回调数据 link {WisdomWallActivity onResultFinish}
     */
    fun finish(resultMedias: List<Media>) {
        onResultMediaListener?.onResultFinish(resultMedias)
    }

    /**
     * 跳转到预览页面 复写onPreviewResult 处理预览回调数据
     */
    fun startPreview(clazz: Class<out WisPreViewActivity>, selectMedia: List<Media>, currentPosition: Int = 0, albumId: String, bundle: Bundle? = null) {
        onResultMediaListener?.startPreview(clazz, selectMedia, currentPosition, albumId, bundle)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Activity.RESULT_OK != resultCode) {
            return
        }
        // the camera callback
        if (CameraStrategy.CAMERA_REQUEST == requestCode) {
            cameraStrategy.onCameraResultAction { onCameraResult(it) }
            //拍完照是不是要去裁剪。
        }

        //预览页面回来
        if (WisPreViewActivity.WIS_PREVIEW_REQUEST_CODE == requestCode) {
            val exit = data!!.getBooleanExtra(WisPreViewActivity.EXTRA_PREVIEW_RESULT_EXIT, false)
            val medias = data.getParcelableArrayListExtra<Media>(WisPreViewActivity.EXTRA_PREVIEW_SELECT_MEDIA)
            handlePreview(exit, medias)
        }
    }

    open fun handlePreview(exit: Boolean, medias: List<Media>) {
        if (exit) {
            onResultMediaListener?.onResultFinish(medias)
            return
        }
        onPreviewResult(medias)
        onResultMediaListener?.onPreViewResult(medias)
    }

    /**
     * **************************下面是必须实现和可复写的方法**************************
     */
    abstract fun startLoading()

    open fun checkPermissionOnDenied(permissionsDeniedForever: Array<String>, permissionsDenied: Array<String>) {}
    open fun onCameraResult(media: Media) {}
    open fun onPreviewResult(medias: List<Media>) {}
}
