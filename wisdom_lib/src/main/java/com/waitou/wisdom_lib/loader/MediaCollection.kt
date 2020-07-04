package com.waitou.wisdom_lib.loader

import android.database.Cursor
import android.os.Bundle
import android.support.annotation.NonNull
import android.support.v4.app.FragmentActivity
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.call.LoaderMedia
import java.lang.ref.WeakReference

/**
 * auth aboom
 * date 2019-05-26
 */
class MediaCollection : LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        private const val ARGS_ALBUM_ID = "album_id"
        private const val ARGS_CAMERA = "camera"
        private const val ARGS_LOAD_ID = 2
    }

    private lateinit var context: WeakReference<FragmentActivity>
    private lateinit var loaderMedia: WeakReference<LoaderMedia>
    private val loaderManager by lazy { context.get()?.supportLoaderManager }


    fun onCreate(@NonNull activity: FragmentActivity, @NonNull loaderMedia: LoaderMedia) {
        this.context = WeakReference(activity)
        this.loaderMedia = WeakReference(loaderMedia)
    }

    @JvmOverloads
    fun loadMedia(albumId: String, isCamera: Boolean = false) {
        val bundle = Bundle()
        bundle.putString(ARGS_ALBUM_ID, albumId)
        bundle.putBoolean(ARGS_CAMERA, isCamera)
        loaderManager?.initLoader(ARGS_LOAD_ID, bundle, this)
    }

    override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<Cursor> {
        val albumId = bundle!!.getString(ARGS_ALBUM_ID)
        val isCamera = bundle.getBoolean(ARGS_CAMERA)
        return MediaLoader.newInstance(context.get()!!, albumId, isCamera)
    }

    override fun onLoadFinished(p0: Loader<Cursor>, cursor: Cursor?) {
        context.get()?.let {
            cursor?.let {
                if (!cursor.isBeforeFirst) {
                    return
                }
                val list = mutableListOf<Media>()
                while (it.moveToNext()) {
                    try {
                        val media = Media.valueOf(cursor)
                        list.add(media)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                loaderMedia.get()?.mediaResult(list)
                loaderManager?.destroyLoader(ARGS_LOAD_ID)
            }
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }
}
