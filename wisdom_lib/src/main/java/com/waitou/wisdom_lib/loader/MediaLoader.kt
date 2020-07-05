package com.waitou.wisdom_lib.loader

import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.provider.MediaStore
import android.support.v4.content.CursorLoader
import com.waitou.wisdom_lib.bean.Album
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.utils.filterMaxFileSize
import com.waitou.wisdom_lib.utils.onlyImages
import com.waitou.wisdom_lib.utils.onlyVideos

/**
 * auth aboom
 * date 2019-05-26
 */
class MediaLoader private constructor(
    context: Context,
    selection: String?,
    selectionArgs: Array<String>?,
    private val isCamera: Boolean
) :
    CursorLoader(
        context, MediaStore.Files.getContentUri("external"),
        PROJECTION, selection, selectionArgs, MediaStore.Images.Media.DATE_MODIFIED + " DESC"
    ) {

    override fun loadInBackground(): Cursor? {
        val cursor = super.loadInBackground()
        //设备不具备相机功能
        if (!isCamera ||
            !context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)
        ) {
            return cursor
        }
        //添加一个相册的item
        val mc = MatrixCursor(PROJECTION)
        mc.addRow(arrayOf(Media.ITEM_ID_CAPTURE, "", "capture", 0, 0))
        return MergeCursor(arrayOf(mc, cursor))
    }

    companion object {
        /**
         * 查询media的字段
         */
        private val PROJECTION = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.MIME_TYPE,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Audio.Media.DURATION
        )

        /**
         * 查询的条件type
         */
        private val SELECTION_ALL_ARGS = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        fun newInstance(context: Context, albumId: String?, isCamera: Boolean): MediaLoader {
            val selectionArgs = mutableListOf<String>()

            var selection = if (onlyImages() || onlyVideos()) {
                "${MediaStore.Files.FileColumns.MEDIA_TYPE}=? AND ${MediaStore.MediaColumns.SIZE}>0"
            } else {
                "(${MediaStore.Files.FileColumns.MEDIA_TYPE}=? OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?) AND ${MediaStore.MediaColumns.SIZE}>0"
            }

            filterMaxFileSize()?.let {
                selection += " AND ${MediaStore.MediaColumns.SIZE}<${it}"
            }

            when {
                onlyImages() -> selectionArgs.add(SELECTION_ALL_ARGS[0])
                onlyVideos() -> selectionArgs.add(SELECTION_ALL_ARGS[1])
                else -> selectionArgs.addAll(SELECTION_ALL_ARGS)
            }

            if (Album.ALBUM_ID_ALL != albumId) {
                selection += " AND ${MediaStore.Images.Media.BUCKET_ID}=?"
                selectionArgs.add(albumId!!)
            }
            return MediaLoader(context, selection, selectionArgs.toTypedArray(), isCamera)
        }
    }
}
