package com.waitou.wisdom_lib.bean

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.waitou.wisdom_lib.config.getMimeType
import com.waitou.wisdom_lib.config.isImage
import com.waitou.wisdom_lib.config.isVideo
import com.waitou.wisdom_lib.loader.AlbumLoader

/**
 * auth aboom
 * date 2019-05-25
 */
class Album(
        /**
         * 主键
         */
        private val mediaId: String,
        /**
         * 相册id
         */
        val albumId: String,
        /**
         * 相册名称
         */
        val albumName: String,
        /**
         * 相册图片路径
         */
        val path: String,
        /**
         * 相册有多少张图
         */
        val count: Int) {

    /**
     * uri
     */
    val uri: Uri

    init {
        val contentUri = when {
            isImage(getMimeType(path)) -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            isVideo(getMimeType(path)) -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Files.getContentUri("external")
        }
        this.uri = ContentUris.withAppendedId(contentUri, mediaId.toLong())
    }

    override fun toString(): String {
        return "Album(mediaId='$mediaId', albumId='$albumId', albumName='$albumName', path='$path', count=$count, uri=$uri)"
    }

    companion object {
        const val ALBUM_ID_ALL = "-1"
        const val ALBUM_NAME_ALL = "All"

        fun valueOf(cursor: Cursor): Album {
            return Album(
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)),
                    cursor.getInt(cursor.getColumnIndexOrThrow(AlbumLoader.COLUMN_COUNT))
            )
        }
    }
}
