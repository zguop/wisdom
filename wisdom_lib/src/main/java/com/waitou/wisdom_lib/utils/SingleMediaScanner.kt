package com.waitou.wisdom_lib.utils

import android.content.ContentUris
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.getMimeType
import java.io.File
import java.lang.ref.WeakReference

/**
 * auth aboom
 * date 2020/5/8
 */
class SingleMediaScanner(context: Context, private val filePath: File, function: (Media) -> Unit) : MediaScannerConnection.MediaScannerConnectionClient {

    private val msc: MediaScannerConnection = MediaScannerConnection(context.applicationContext, this)
    private val mimeType: String = getMimeType(filePath.absolutePath)
    private val functionWeakReference: WeakReference<(Media) -> Unit> = WeakReference(function)

    init {
        msc.connect()
    }

    override fun onMediaScannerConnected() {
        msc.scanFile(filePath.absolutePath, mimeType)
    }

    override fun onScanCompleted(path: String, uri: Uri) {
        msc.disconnect()
        val mediaId = ContentUris.parseId(uri).toString()
        val duration = CameraStrategy.getDuration(path)
        functionWeakReference.get()?.invoke(Media(mediaId, mimeType, path, filePath.length(), duration))
    }
}