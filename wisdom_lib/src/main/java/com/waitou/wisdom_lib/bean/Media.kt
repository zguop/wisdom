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
import com.waitou.wisdom_lib.loader.MediaLoader
import java.lang.IllegalArgumentException

/**
 * auth aboom
 * date 2019-05-26
 */
class Media(
    /**
     * id
     */
    var mediaId: Long,
    /**
     * type
     */
    var mineType: String,
    /**
     * file name
     */
    var displayName: String,
    /**
     * path
     */
    var path: String,
    /**
     * size
     */
    var size: Long,
    /**
     * image or video
     */
    var width: Int,
    var height: Int,
    var orientation: Int,
    /**
     * video duration
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
    var cropUri: Uri? = null

    /**
     * CompressEngine compress
     */
    var compressUri: Uri? = null

    init {
        val contentUri = when {
            isImage() -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            isVideo() -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            else -> MediaStore.Files.getContentUri("external")
        }
        uri = ContentUris.withAppendedId(contentUri, mediaId)
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

    fun compressNullToUri(): Uri {
        return compressUri ?: uri
    }

    fun cropNullToUri(): Uri {
        return cropUri ?: uri
    }

    fun compressOrCropNullToUri(): Uri {
        return compressUri ?: cropUri ?: uri
    }


    companion object {
        const val ITEM_ID_CAPTURE: Long = -1

        fun valueOf(cursor: Cursor): Media {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID))
            val mineType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE))
            val displayName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
            val data = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA))
            val size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
            val width = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.WIDTH))
            val height = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.HEIGHT))
            val orientation = cursor.getInt(cursor.getColumnIndexOrThrow(MediaLoader.ORIENTATION))
            val duration = try {
                cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION))
            } catch (e: IllegalArgumentException) {
                0
            }
            return Media(id, mineType, displayName, data, size, width, height, orientation, duration)
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
        parcel.writeString(displayName)
        parcel.writeString(path)
        parcel.writeLong(size)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeInt(orientation)
        parcel.writeLong(duration)
        parcel.writeParcelable(cropUri, 0)
        parcel.writeParcelable(compressUri, 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    private constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readLong()
    ) {
        cropUri = parcel.readParcelable(Uri::class.java.classLoader)
        compressUri = parcel.readParcelable(Uri::class.java.classLoader)
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

    override fun toString(): String {
        return "Media(mediaId=$mediaId, mineType='$mineType', displayName='$displayName', path='$path', size=$size, width=$width, height=$height, orientation=$orientation, duration=$duration, uri=$uri, cropUri=$cropUri, compressUri=$compressUri)"
    }
}
