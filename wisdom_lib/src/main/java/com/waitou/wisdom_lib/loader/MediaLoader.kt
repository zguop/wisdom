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
        context,
        MediaStore.Files.getContentUri("external"),
        PROJECTION,
        selection,
        selectionArgs,
        MediaStore.Images.Media.DATE_MODIFIED + " DESC"
    ) {

    override fun loadInBackground(): Cursor? {
        val cursor = super.loadInBackground()
        //设备不具备相机功能
        if (!isCamera || !context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            return cursor
        }
        //添加一个相机的item
        val mc = MatrixCursor(PROJECTION)
        mc.addRow(arrayOf(Media.ITEM_ID_CAPTURE, "", "", "", "capture", 0, 0))
        return MergeCursor(arrayOf(mc, cursor))
    }

    companion object {
        /**
         * 查询media的字段
         */
        private val PROJECTION = arrayOf(
            MediaStore.MediaColumns._ID,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DURATION
        )

        fun newInstance(context: Context, albumId: String?, isCamera: Boolean): MediaLoader {
            val selectionSql = StringBuilder()
            val selectionArgs = mutableListOf<String>()
            SQLSelection.format(selectionSql, selectionArgs)
            if (Album.ALBUM_ID_ALL != albumId) {
                selectionArgs.add(albumId!!)
                selectionSql.append(" AND ${MediaStore.MediaColumns.BUCKET_ID}=?")
            }
            return MediaLoader(context, selectionSql.toString(), selectionArgs.toTypedArray(), isCamera)
        }
    }
}
