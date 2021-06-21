package com.waitou.wisdom_lib.bean

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.waitou.wisdom_lib.config.isImage
import com.waitou.wisdom_lib.config.isVideo
import com.waitou.wisdom_lib.loader.AlbumLoader

/**
 * auth aboom
 * date 2019-05-25
 */
class Album(
    /**
     * 图片id
     */
    var mediaId: Long,
    /**
     * 相册id
     */
    var albumId: String,
    /**
     * 相册名称
     */
    var albumName: String,

    /**
     * 相册图片类型
     */
    var mineType: String,
    /**
     * 相册有多少张图
     */
    var count: Int
) {

    /**
     * uri
     */
    var uri: Uri

    init {
        val contentUri = when {
            isImage(mineType) -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            isVideo(mineType) -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Files.getContentUri("external")
        }
        this.uri = ContentUris.withAppendedId(contentUri, mediaId)
    }

    override fun toString(): String {
        return "Album(mediaId='$mediaId', albumId='$albumId', albumName='$albumName', count=$count, uri=$uri)"
    }

    companion object {
        const val ALBUM_ID_ALL = "-1"
        const val ALBUM_NAME_ALL = "All"

        fun valueOf(cursor: Cursor): Album {
            return Album(
                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(AlbumLoader.COLUMN_COUNT))
            )
        }
    }
}
