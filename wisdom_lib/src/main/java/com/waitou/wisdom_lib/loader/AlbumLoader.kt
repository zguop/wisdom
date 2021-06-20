package com.waitou.wisdom_lib.loader

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.provider.MediaStore
import android.provider.MediaStore.Images.ImageColumns.BUCKET_ID
import android.support.v4.content.CursorLoader
import android.util.Log
import com.waitou.wisdom_lib.bean.Album
import com.waitou.wisdom_lib.utils.*

/**
 * auth aboom
 * date 2019-05-25
 */
class AlbumLoader private constructor(
    context: Context,
    selection: String,
    selectionArgs: Array<String>
) :
    CursorLoader(
        context, MediaStore.Files.getContentUri("external"),
        PROJECTION, selection, selectionArgs, MediaStore.Images.Media.DATE_MODIFIED + " DESC"
    ) {

    //加载图片目录
    override fun loadInBackground(): Cursor {
        val cursor = super.loadInBackground()
        //创建一张虚拟表，表字段包含 COLUMNS
        val allAlbum = MatrixCursor(COLUMNS)
        //得到 文件夹下的图片总数
        var totalCount = 0
        //第一张图片的id
        var id = Album.ALBUM_ID_ALL.toLong()
        //封面类型
        var mineType = ""
        if (cursor != null) {
            while (cursor.moveToNext()) {
                totalCount += cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT))
            }
            if (cursor.moveToFirst()) {
                id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
                mineType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE))
            }
        }
        //插入一条记录到虚拟表
        allAlbum.addRow(
            arrayOf(
                id,
                Album.ALBUM_ID_ALL,
                Album.ALBUM_NAME_ALL,
                mineType,
                totalCount.toString()
            )
        )
        //合并结果集
        return MergeCursor(arrayOf(allAlbum, cursor))
    }

    companion object {
        const val COLUMN_COUNT = "count"

        /**
         * 查询表的字段
         */
        private val PROJECTION = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Images.Media.BUCKET_ID,//相册id
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, //相册名称
            MediaStore.MediaColumns.MIME_TYPE,
            "COUNT(*) AS $COLUMN_COUNT"
        )

        /**
         * 虚拟表字段结构
         */
        private val COLUMNS = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            COLUMN_COUNT
        )

        fun newInstance(context: Context): CursorLoader {
            val selectionSql = StringBuilder()
            val selectionArgs = mutableListOf<String>()
            SQLSelection.format(selectionSql, selectionArgs)
            selectionSql.append(") GROUP BY (${MediaStore.Images.Media.BUCKET_ID}")
            return AlbumLoader(context, selectionSql.toString(), selectionArgs.toTypedArray())
        }
    }
}


