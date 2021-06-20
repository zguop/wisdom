package com.waitou.wisdom_lib.loader

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.database.MergeCursor
import android.provider.MediaStore
import android.support.v4.content.CursorLoader
import android.util.Log
import com.waitou.wisdom_lib.bean.Album
import com.waitou.wisdom_lib.utils.isAndroidQ
import kotlin.math.log

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
        context,
        MediaStore.Files.getContentUri("external"),
        if (isAndroidQ()) PROJECTION_Q else PROJECTION,
        selection,
        selectionArgs,
        MediaStore.Images.Media.DATE_MODIFIED + " DESC"
    ) {

    //加载图片目录
    override fun loadInBackground(): Cursor {
        val cursor = super.loadInBackground()
        //创建一张虚拟表，表字段包含 COLUMNS
        val allAlbum = MatrixCursor(COLUMNS)
        //得到 文件夹下的图片总数
        var totalCount = 0
        //第一张图片的id
        var firstId = 0L
        //封面类型
        var firstMineType = ""

        if (isAndroidQ()) {
            val otherAlbums = MatrixCursor(COLUMNS)
            val albumCount = hashMapOf<String, Int>()
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    val bucketId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID))
                    albumCount[bucketId] = albumCount[bucketId]?.let { c -> c + 1 } ?: 1
                }
                if (cursor.moveToFirst()) {
                    val edSet = mutableSetOf<String>()
                    do {
                        val bucketId = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.BUCKET_ID))
                        if (edSet.contains(bucketId)) continue
                        edSet.add(bucketId)
                        val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                        val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME))
                        val mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE))
                        val count = albumCount[bucketId]!!
                        if (cursor.isFirst) {
                            firstId = id
                            firstMineType = mimeType
                        }
                        otherAlbums.addRow(arrayOf(
                            id,
                            bucketId,
                            displayName,
                            mimeType,
                            count
                        ))
                        totalCount += count
                    } while (cursor.moveToNext())
                }
            }
            allAlbum.addRow(
                arrayOf(
                    firstId,
                    Album.ALBUM_ID_ALL,
                    Album.ALBUM_NAME_ALL,
                    firstMineType,
                    totalCount
                )
            )
            return MergeCursor(arrayOf(allAlbum, otherAlbums))
        } else {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    totalCount += cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COUNT))
                }
                if (cursor.moveToFirst()) {
                    firstId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
                    firstMineType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
                }
            }
            //插入一条记录到虚拟表
            allAlbum.addRow(
                arrayOf(
                    firstId,
                    Album.ALBUM_ID_ALL,
                    Album.ALBUM_NAME_ALL,
                    firstMineType,
                    totalCount
                )
            )
            //合并结果集
            return MergeCursor(arrayOf(allAlbum, cursor))
        }
    }

    companion object {
        const val COLUMN_COUNT = "count"

        /**
         * 查询表的字段
         */
        private val PROJECTION = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            "COUNT(*) AS $COLUMN_COUNT"
        )

        /**
         * 查询表的字段
         */
        private val PROJECTION_Q = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE
        )

        /**
         * 虚拟表字段结构
         */
        private val COLUMNS = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.MediaColumns.BUCKET_ID,
            MediaStore.MediaColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            COLUMN_COUNT
        )


        fun newInstance(context: Context): CursorLoader {
            val selectionSql = StringBuilder()
            val selectionArgs = mutableListOf<String>()
            SQLSelection.format(selectionSql, selectionArgs)
            if (!isAndroidQ()) {
                selectionSql.append(") GROUP BY (${MediaStore.MediaColumns.BUCKET_ID}")
            }
            return AlbumLoader(context, selectionSql.toString(), selectionArgs.toTypedArray())
        }
    }
}


