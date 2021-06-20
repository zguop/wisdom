package com.waitou.wisdom_lib.loader

import android.database.Cursor
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.interfaces.LoaderMedia
import java.lang.ref.WeakReference

/**
 * auth aboom
 * date 2019-05-26
 */
class MediaCollection(activity: FragmentActivity, loaderMedia: LoaderMedia) : LoaderManager.LoaderCallbacks<Cursor> {

    companion object {
        private const val ARGS_ALBUM_ID = "album_id"
        private const val ARGS_CAMERA = "camera"
        private const val ARGS_LOAD_ID = 2
    }

    private val loaderReference = WeakReference(loaderMedia)
    private val loaderManager = LoaderManager.getInstance(activity)
    private val context = activity.applicationContext

    @JvmOverloads
    fun loadMedia(albumId: String, isCamera: Boolean = false) {
        val bundle = Bundle()
        bundle.putString(ARGS_ALBUM_ID, albumId)
        bundle.putBoolean(ARGS_CAMERA, isCamera)
        loaderManager.initLoader(ARGS_LOAD_ID, bundle, this)
    }

    override fun onCreateLoader(id: Int, bundle: Bundle?): Loader<Cursor> {
        val albumId = bundle!!.getString(ARGS_ALBUM_ID)
        val isCamera = bundle.getBoolean(ARGS_CAMERA)
        return MediaLoader.newInstance(context, albumId, isCamera)
    }

    override fun onLoadFinished(p0: Loader<Cursor>, cursor: Cursor?) {
        try {
            cursor?.let {
                if (!it.isBeforeFirst) {
                    return
                }
                val list = mutableListOf<Media>()
                while (it.moveToNext()) {
                    val media = Media.valueOf(cursor)
                    list.add(media)
                }

//                if (albumId == Album.ALBUM_ID_ALL) {
//                    val albumCount = hashMapOf<String, Int>()
//                    val albumList = mutableListOf<Album>()
//                    if (it.moveToFirst()) {
//                        do {
//                            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
//                            if (id != Media.ITEM_ID_CAPTURE) {
//                                val bucketId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID))
//                                val count = albumCount[bucketId]
//                                if (count == null) {
//                                    albumCount[bucketId] = 1
//                                    albumList.add(Album.valueOf(it))
//                                } else {
//                                    albumCount[bucketId] = count + 1
//                                }
//                            }
//                        } while (it.moveToNext())
//
//                        var totalCount = 0
//                        albumList.forEach { album ->
//                            album.count = albumCount[album.albumId] ?: 0
//                            totalCount += album.count
//                        }
//
//                        val album = albumList[0]
//                        albumList.add(0, Album(album.mediaId, Album.ALBUM_ID_ALL, Album.ALBUM_NAME_ALL, album.mineType, totalCount))
//
//                        loaderReference.get()?.albumResult(albumList)
//                    }
//                }
                loaderReference.get()?.mediaResult(list)
                loaderManager.destroyLoader(ARGS_LOAD_ID)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onLoaderReset(loader: Loader<Cursor>) {

    }
}
