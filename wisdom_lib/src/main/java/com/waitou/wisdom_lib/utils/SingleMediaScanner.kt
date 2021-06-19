package com.waitou.wisdom_lib.utils

import android.content.ContentUris
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.isVideo
import java.io.File
import java.lang.ref.WeakReference

/**
 * auth aboom
 * date 2020/5/8
 */
class SingleMediaScanner(private val applicationContext: Context, private val filePath: File, function: (Media) -> Unit) : MediaScannerConnection.MediaScannerConnectionClient {

    private val msc: MediaScannerConnection = MediaScannerConnection(applicationContext, this)
    private val functionWeakReference: WeakReference<(Media) -> Unit> = WeakReference(function)

    init {
        msc.connect()
    }

    override fun onMediaScannerConnected() {
        msc.scanFile(filePath.absolutePath, null)
    }

    override fun onScanCompleted(path: String, uri: Uri) {
        msc.disconnect()
        val mediaId = ContentUris.parseId(uri)
        val mimeType = applicationContext.contentResolver.getType(uri).orEmpty().ifEmpty { "image/jpeg" }
        val duration = if (isVideo(mimeType)) CameraStrategy.getDuration(path) else 0
        functionWeakReference.get()?.invoke(Media(mediaId, mimeType, path, filePath.length(), duration))
    }
}