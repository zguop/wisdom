package com.waitou.wisdom_lib.utils

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import java.io.File
import java.lang.ref.WeakReference

/**
 * auth aboom
 * date 2020/5/8
 */
class SingleMediaScanner(context: Context, private val filePath: File, scanCompleted: (Uri, String) -> Unit) : MediaScannerConnection.MediaScannerConnectionClient {

    private val applicationContext = context.applicationContext
    private val msc: MediaScannerConnection = MediaScannerConnection(applicationContext, this)
    private val functionWeakReference: WeakReference<(Uri, String) -> Unit> = WeakReference(scanCompleted)

    init {
        msc.connect()
    }

    override fun onMediaScannerConnected() {
        msc.scanFile(filePath.absolutePath, null)
    }

    override fun onScanCompleted(path: String, uri: Uri) {
        msc.disconnect()
        functionWeakReference.get()?.invoke(uri, path)
    }
}