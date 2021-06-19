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
    var mediaId: Long,
    /**
     * type
     */
    var mineType: String,
    /**
     * path
     */
    var path: String,
    /**
     * size
     */
    var size: Long,
    /**
     * video in ms
     */
    var duration: Long
) : Parcelable {

    /**
     * path
     */
    val uri: Uri

    /**
     * CropEngine crop
     */
    var cropPath: String? = null

    /**
     * CompressEngine compress
     */
    var compressPath: String? = null

    init {
        val contentUri = when {
            isImage() -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            isVideo() -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Files.getContentUri("external")
        }
        uri = try {
            ContentUris.withAppendedId(contentUri, mediaId)
        } catch (e: Exception) {
            Uri.parse(path)
        }
    }

    fun isImage(): Boolean {
        return isImage(mineType)
    }

    fun isVideo(): Boolean {
        return isVideo(mineType)
    }

    fun isGif(): Boolean {
        return isGif(mineType)
    }

    fun compressNullToPath(): String {
        return compressPath ?: path
    }

    fun cropNullToPath(): String {
        return cropPath ?: path
    }

    fun compressOrCropNullToPath(): String {
        return compressPath ?: cropPath ?: path
    }

    override fun toString(): String {
        return "Media(mediaId='$mediaId', mediaType='$mineType', path='$path', size=$size, duration=$duration, uri=$uri, cropPath=$cropPath, compressPath=$compressPath)"
    }

    companion object {
        const val ITEM_ID_CAPTURE: Long = -1

        fun valueOf(cursor: Cursor): Media {
            return Media(
                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)),
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
        parcel.writeLong(mediaId)
        parcel.writeString(mineType)
        parcel.writeString(path)
        parcel.writeLong(size)
        parcel.writeLong(duration)
        parcel.writeString(cropPath)
        parcel.writeString(compressPath)
    }

    override fun describeContents(): Int {
        return 0
    }

    private constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readLong()
    ) {
        cropPath = parcel.readString()
        compressPath = parcel.readString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Media
        if (mediaId != other.mediaId) return false
        if (path != other.path) return false
        if (uri != other.uri) return false
        return true
    }

    override fun hashCode(): Int {
        var result = mediaId.hashCode()
        result = 31 * result + path.hashCode()
        result = 31 * result + uri.hashCode()
        return result
    }
}
