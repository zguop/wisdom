package com.waitou.wisdom_lib.utils

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.getMimeType
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

/**
 * auth aboom
 * date 2019-05-29
 */


class CameraStrategy(fragment: Fragment) {

    private val weakReference = WeakReference<Fragment>(fragment)
    private var functionWeakReference: WeakReference<(Media) -> Unit>? = null
    private lateinit var filePath: File

    fun startCamera(context: Context, authority: String, directory: String?) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(context.packageManager)?.let {
            filePath = checkImageFileExistsAndCreate(directory, "IMAGE_%s.jpg") ?: return@let
            val uriForFile = FileProvider.getUriForFile(context, authority, filePath)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            weakReference.get()?.startActivityForResult(intent, CAMERA_REQUEST)
        }
    }

    fun startCameraVideo(context: Context, authority: String, directory: String?) {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)  // 表示跳转至相机的录视频界面
        intent.resolveActivity(context.packageManager)?.let {
            filePath = checkImageFileExistsAndCreate(directory, "VIDEO_%s.mp4") ?: return@let
            val uriForFile = FileProvider.getUriForFile(context, authority, filePath)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile)
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)  //视频时长
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1) //视频质量 0 - 1
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            weakReference.get()?.startActivityForResult(intent, CAMERA_REQUEST)
        }
    }

    /**
     * 方法名 参数 返回值
     */
    fun onCameraResultAction(function: (Media) -> Unit) {
        //匿名内部类内存泄露啦 处理一下
        functionWeakReference = WeakReference(function)
        weakReference.get()?.activity?.let { context ->
            //使用系统API，获取URL路径中文件的后缀名（扩展名）.jpg .mp4
            val absolutePath = filePath.absolutePath
            val mimeType = getMimeType(absolutePath)
            MediaScannerConnection.scanFile(context.application, arrayOf(absolutePath), arrayOf(mimeType)) { path, uri ->
                functionWeakReference?.get()?.let {
                    val mediaId = ContentUris.parseId(uri).toString()
                    //获取时长
                    val duration = getDuration(path)
                    it.invoke(Media(mediaId, mimeType, path, filePath.length(), duration))
                }
            }
        }
    }

    companion object {
        internal const val CAMERA_REQUEST = 0X11

        /**
         * 获取/storage/emulated/0/Pictures/{directory}/{fileName}
         * @param directory 目录
         * @param formatStr IMAGE_%s.jpg
         *
         */
        @JvmStatic
        fun checkImageFileExistsAndCreate(directory: String?, formatStr: String): File? {
            if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                try {
                    //the path of /storage/emulated/0/Pictures
                    var storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    if (!directory.isNullOrEmpty()) {
                        storageDir = File(storageDir, directory)
                    }
                    if (!storageDir.exists()) {
                        storageDir.mkdirs()
                    }
                    val fileName = String.format(
                            formatStr,
                            SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
                                    .format(Date())
                    )
                    return File(storageDir, fileName)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            return null
        }

        /**
         * 获取视频的播放时长
         */
        @JvmStatic
        fun getDuration(path: String): Long {
            return try {
                val mmr = MediaMetadataRetriever()
                mmr.setDataSource(path)
                return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION).toLong()
            } catch (e: Exception) {
                0
            }
        }
    }
}




