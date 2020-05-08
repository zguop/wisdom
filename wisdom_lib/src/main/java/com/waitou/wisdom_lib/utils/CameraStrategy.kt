package com.waitou.wisdom_lib.utils

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import java.io.File
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

/**
 * auth aboom
 * date 2019-05-29
 */


class CameraStrategy(fragment: Fragment) {

    private val weakReference = WeakReference(fragment)
    lateinit var filePath: File

    fun startCamera(context: Context, authority: String, directory: String?) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(context.packageManager)?.let {
            filePath = getImageFileExistsAndCreate(context, "IMAGE_%s.jpg", directory)
            val uriForFile = FileProvider.getUriForFile(context, authority, filePath)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            weakReference.get()?.startActivityForResult(intent, CAMERA_REQUEST)
        }
    }

    fun startCameraVideo(context: Context, authority: String, directory: String?) {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)  // 表示跳转至相机的录视频界面
        intent.resolveActivity(context.packageManager)?.let {
            filePath = getImageFileExistsAndCreate(context, "VIDEO_%s.mp4", directory)
            val uriForFile = FileProvider.getUriForFile(context, authority, filePath)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriForFile)
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)  //视频时长
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1) //视频质量 0 - 1
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            weakReference.get()?.startActivityForResult(intent, CAMERA_REQUEST)
        }
    }

    companion object {
        const val CAMERA_REQUEST = 0X11

        /**
         * /storage/emulated/0/Pictures/{directory}/{fileName}
         * @param directory 目录
         * @param formatStr IMAGE_%s.jpg
         *
         */
        @JvmStatic
        @JvmOverloads
        fun getImageFileExistsAndCreate(
            context: Context,
            formatStr: String,
            directory: String? = null
        ): File {
            var storageDir = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState())
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) else
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            if (!directory.isNullOrEmpty()) {
                storageDir = File(storageDir, directory)
            }
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            return File(storageDir, getFileName(formatStr))
        }

        @JvmStatic
        fun getFileName(formatStr: String): String {
            return String.format(
                formatStr,
                SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
                    .format(Date())
            )
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




