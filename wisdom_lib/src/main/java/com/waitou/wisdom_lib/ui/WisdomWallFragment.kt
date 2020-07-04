package com.waitou.wisdom_lib.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.View
import com.waitou.wisdom_lib.bean.Album
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.call.LoaderAlbum
import com.waitou.wisdom_lib.call.LoaderMedia
import com.waitou.wisdom_lib.call.OnMediaListener
import com.waitou.wisdom_lib.config.WisdomConfig
import com.waitou.wisdom_lib.loader.AlbumCollection
import com.waitou.wisdom_lib.loader.MediaCollection
import com.waitou.wisdom_lib.utils.CameraStrategy
import com.waitou.wisdom_lib.utils.CropStrategy
import com.waitou.wisdom_lib.utils.SingleMediaScanner

/**
 * auth aboom
 * date 2019-05-24
 */
abstract class WisdomWallFragment : Fragment(), LoaderAlbum, LoaderMedia {

    companion object {
        @JvmField
        val TAG: String = WisdomWallFragment::class.java.name + hashCode()
    }

    var onResultMediaListener: OnMediaListener? = null
    lateinit var currentAlbumId: String

    private val albumCollection by lazy { AlbumCollection() }
    private val mediaCollection by lazy { MediaCollection() }
    private val cameraStrategy by lazy { CameraStrategy() }
    private val cropStrategy by lazy { CropStrategy() }

    private var cameraPermissionGranted: (() -> Unit)? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            albumCollection.onCreate(it, this)
            mediaCollection.onCreate(it, this)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermissionOnStart()
        beforeSelectorMedias(WisdomConfig.getInstance().imgMedias)
    }

    private fun checkPermissionOnStart() {
        activity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                )
            } else {
                startLoading()
            }
        }
    }

    private fun checkPermissionOnCamera(cameraPermissionGranted: (() -> Unit)?) {
        activity?.let {
            if (ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                this.cameraPermissionGranted = cameraPermissionGranted
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                )
            } else {
                cameraPermissionGranted?.invoke()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (permissions.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
        mediaCollection.loadMedia(albumId, WisdomConfig.getInstance().isCamera)
    }

    /**
     * 打开相机拍照 复写onCameraResult方法接收相机回调
     */
    fun startCameraImage() {
        checkPermissionOnCamera {
            cameraStrategy.startCamera(
                this,
                WisdomConfig.getInstance().authorities,
                WisdomConfig.getInstance().directory
            )
        }
    }

    /**
     * 打开相机录像，复写onCameraResult方法接收相机回调
     */
    fun startCameraVideo() {
        checkPermissionOnCamera {
            cameraStrategy.startCameraVideo(
                this,
                WisdomConfig.getInstance().authorities,
                WisdomConfig.getInstance().directory
            )
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
    fun startPreview(
        clazz: Class<out WisPreViewActivity>,
        selectMedia: List<Media>,
        currentPosition: Int = 0,
        albumId: String,
        bundle: Bundle? = null
    ) {
        onResultMediaListener?.startPreview(clazz, selectMedia, currentPosition, albumId, bundle)
    }

    /**
     * 配置了cropEngine进行裁剪
     */
    fun startCrop(media: Media): Boolean {
        WisdomConfig.getInstance().cropEngine ?: return false
        cropStrategy.startCrop(this, media)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (Activity.RESULT_OK != resultCode) {
            return
        }
        // the camera callback
        if (CameraStrategy.CAMERA_REQUEST == requestCode) {
            SingleMediaScanner(activity!!, cameraStrategy.filePath) {
                onCameraResult(it)
            }
        }
        // the crop callback
        WisdomConfig.getInstance().cropEngine?.let {
            if (cropStrategy.startCropRequest == requestCode) {
                onCropResult(cropStrategy.cropResult(data))
            }
        }
        //预览页面回来
        if (WisPreViewActivity.WIS_PREVIEW_REQUEST_CODE == requestCode) {
            val exit = data!!.getBooleanExtra(WisPreViewActivity.EXTRA_PREVIEW_RESULT_EXIT, false)
            val medias =
                data.getParcelableArrayListExtra<Media>(WisPreViewActivity.EXTRA_PREVIEW_SELECT_MEDIA)
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

    /**
     * 权限拒绝了回调，包括读写，相机打开权限
     * @param permissionsDeniedForever 集合包含了用户永久拒绝了权限
     * @param permissionsDenied 集合包含了用户拒绝了权限
     */
    open fun checkPermissionOnDenied(permissionsDeniedForever: Array<String>, permissionsDenied: Array<String>) {}

    /**
     * 默认勾选medias，onActivityCreated调用
     */
    open fun beforeSelectorMedias(imgMedias: List<Media>?) {}

    /**
     * 相机拍照或者录像后回调，包装成一个media
     */
    open fun onCameraResult(media: Media) {}

    /**
     * 裁剪完成后，调用
     */
    open fun onCropResult(media: Media) {}

    /**
     * 预览页面回调，在预览页面做了的事情，将数据源回调更新
     */
    open fun onPreviewResult(medias: List<Media>) {}
}
