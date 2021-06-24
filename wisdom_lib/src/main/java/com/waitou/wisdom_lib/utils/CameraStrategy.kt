package com.waitou.wisdom_lib.utils

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.waitou.wisdom_lib.config.MIME_TYPE_IMAGE_JPEG
import com.waitou.wisdom_lib.config.MIME_TYPE_VIDEO_MP4
import com.waitou.wisdom_lib.config.isImage
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * auth aboom
 * date 2019-05-29
 */


class CameraStrategy {

    lateinit var filePath: File
    lateinit var fileUri: Uri

    fun startCamera(fragment: Fragment, authority: String, directory: String?) {
        fragment.activity?.let { context ->
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val fileUri = if (isAndroidQ()) {
                val uri = createImageUri(context, CONST_FORMAT_IMAGE, MIME_TYPE_IMAGE_JPEG, directory.orEmpty()) ?: return
                uri.also { fileUri = it }
            } else {
                filePath = createImageFile(context, CONST_FORMAT_IMAGE, MIME_TYPE_IMAGE_JPEG, directory.orEmpty())
                FileProvider.getUriForFile(context, authority, filePath)
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            fragment.startActivityForResult(intent, CAMERA_REQUEST)
        }
    }

    fun startCameraVideo(fragment: Fragment, authority: String, directory: String?) {
        fragment.activity?.let { context ->
            val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)  // 表示跳转至相机的录视频界面
            val fileUri = if (isAndroidQ()) {
                val uri = createImageUri(context, CONST_FORMAT_VIDEO, MIME_TYPE_VIDEO_MP4, directory.orEmpty()) ?: return
                uri.also { fileUri = it }
            } else {
                filePath = createImageFile(context, CONST_FORMAT_VIDEO, MIME_TYPE_VIDEO_MP4, directory.orEmpty())
                FileProvider.getUriForFile(context, authority, filePath)
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
            intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30)  //视频时长
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1) //视频质量 0 - 1
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            fragment.startActivityForResult(intent, CAMERA_REQUEST)
        }
    }

    companion object {
        const val CONST_FORMAT_IMAGE = "IMAGE_%s.jpg"
        const val CONST_FORMAT_VIDEO = "VIDEO_%s.mp4"
        const val CONST_CAPTURE = "Capture"
        const val CAMERA_REQUEST = 0X11

        /**
         * /storage/emulated/0/DCIM/{directory}/{fileName}
         * /storage/emulated/0/Movies/{directory}/{fileName}
         * @param directory 目录
         * @param formatStr [CONST_FORMAT_IMAGE][CONST_FORMAT_VIDEO]
         * @param mimeType [MIME_TYPE_IMAGE_JPEG][MIME_TYPE_VIDEO_MP4]
         */
        @JvmStatic
        @JvmOverloads
        fun createImageFile(
            context: Context,
            formatStr: String,
            mimeType: String,
            directory: String = CONST_CAPTURE
        ): File {
            val rootDir = if (isImage(mimeType)) {
                Environment.DIRECTORY_DCIM
            } else {
                Environment.DIRECTORY_MOVIES
            }
            val storageDir = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
                Environment.getExternalStoragePublicDirectory(rootDir)
            } else {
                context.getExternalFilesDir(rootDir)
            }
            val dir = if (directory.isNotEmpty()) File(storageDir, directory) else storageDir
            if (!dir!!.exists()) {
                dir.mkdirs()
            }
            return File(dir, getFileName(formatStr))
        }

        /**
         * /storage/emulated/0/DCIM/{directory}/{fileName}
         * /storage/emulated/0/Movies/{directory}/{fileName}
         * @param directory 目录
         * @param formatStr [CONST_FORMAT_IMAGE][CONST_FORMAT_VIDEO]
         * @param mimeType [MIME_TYPE_IMAGE_JPEG][MIME_TYPE_VIDEO_MP4]
         */
        @JvmStatic
        @JvmOverloads
        fun createImageUri(
            context: Context,
            formatStr: String,
            mimeType: String,
            directory: String = CONST_CAPTURE
        ): Uri? {
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, getFileName(formatStr))
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            val path = if (isImage(mimeType)) {
                if (directory.isEmpty()) Environment.DIRECTORY_DCIM else "${Environment.DIRECTORY_DCIM}/$directory"
            } else {
                if (directory.isEmpty()) Environment.DIRECTORY_MOVIES else "${Environment.DIRECTORY_MOVIES}/$directory"
            }
            values.put(MediaStore.Images.Media.RELATIVE_PATH, path)

            val contentUri = if (isImage(mimeType)) {
                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else {
                    MediaStore.Images.Media.INTERNAL_CONTENT_URI
                }
            } else {
                if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else {
                    MediaStore.Video.Media.INTERNAL_CONTENT_URI
                }
            }
            return context.applicationContext.contentResolver.insert(contentUri, values)
        }

        @JvmStatic
        fun getFileName(formatStr: String): String {
            return String.format(
                formatStr,
                SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.getDefault())
                    .format(Date())
            )
        }
    }
}




