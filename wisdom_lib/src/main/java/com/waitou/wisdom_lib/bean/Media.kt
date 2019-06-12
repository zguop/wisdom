package com.waitou.wisdom_lib.bean

import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import com.waitou.wisdom_lib.config.isGif
import com.waitou.wisdom_lib.config.isImage
import com.waitou.wisdom_lib.config.isVideo

/**
 * auth aboom
 * date 2019-05-26
 */
class Media(
        /**
         * 主键
         */
        val mediaId: String,
        /**
         * type
         */
        val mediaType: String,
        /**
         * path
         */
        val path: String,
        /**
         * size
         */
        val size: Long,
        /**
         * video in ms
         */
        val duration: Long
) : Parcelable {

    /**
     * path
     */
    val uri: Uri

    init {
        val contentUri = when {
            isImage() -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            isVideo() -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Files.getContentUri("external")
        }
        this.uri = ContentUris.withAppendedId(contentUri, mediaId.toLong())
    }

    fun isCapture(): Boolean {
        return ITEM_ID_CAPTURE == mediaId
    }

    fun isImage(): Boolean {
        return isImage(mediaType)
    }

    fun isVideo(): Boolean {
        return isVideo(mediaType)
    }

    fun isGif(): Boolean {
        return isGif(mediaType)
    }


    override fun toString(): String {
        return "Media(mediaId='$mediaId', mediaType='$mediaType', path='$path', size=$size, duration=$duration, uri=$uri)"
    }

    companion object {
        const val ITEM_ID_CAPTURE: String = "-1"

        fun valueOf(cursor: Cursor): Media {
            return Media(
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)),
                    cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))
            )
        }


        @JvmField
        val CREATOR = object : Parcelable.Creator<Media> {
            override fun createFromParcel(parcel: Parcel): Media {
                return Media(parcel)
            }

            override fun newArray(size: Int): Array<Media?> {
                return arrayOfNulls(size)
            }
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(mediaId)
        parcel.writeString(mediaType)
        parcel.writeString(path)
        parcel.writeLong(size)
        parcel.writeLong(duration)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Media
        if (mediaId != other.mediaId) return false
        if (mediaType != other.mediaType) return false
        if (path != other.path) return false
        if (size != other.size) return false
        if (duration != other.duration) return false
        if (uri != other.uri) return false
        return true
    }

    override fun hashCode(): Int {
        var result = mediaId.hashCode()
        result = 31 * result + mediaType.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + size.hashCode()
        result = 31 * result + duration.hashCode()
        result = 31 * result + uri.hashCode()
        return result
    }

    private constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readLong(),
            parcel.readLong()
    )
}
