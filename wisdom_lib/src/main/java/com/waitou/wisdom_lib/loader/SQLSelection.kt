package com.waitou.wisdom_lib.loader

import android.provider.MediaStore
import com.waitou.wisdom_lib.utils.*

/**
 * auth aboom
 * date 2021/6/19
 */
internal class SQLSelection {
    companion object {
        /**
         * SELECT _id, bucket_id, bucket_display_name, _data, COUNT(*) AS count FROM files WHERE
         *
         * ((media_type=? AND _size<=? OR media_type=? AND _size<=?) AND _size>0) GROUP BY (bucket_id) ORDER BY date_modified DESC
         */
        fun format(builder: StringBuilder, args: MutableList<String>) {
            when {
                onlyImages() -> {
                    //media_type=? AND _size>0 AND _size<=?)
                    args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
                    builder.append("${MediaStore.Files.FileColumns.MEDIA_TYPE}=? AND ${MediaStore.MediaColumns.SIZE}>0")
                    hasImageMaxSizeConfig {
                        args.add(it.toString())
                        builder.append(" AND ${MediaStore.MediaColumns.SIZE}<=?")
                    }
                }
                onlyVideos() -> {
                    //media_type=? AND _size>0 AND _size<=?)
                    args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
                    builder.append("${MediaStore.Files.FileColumns.MEDIA_TYPE}=? AND ${MediaStore.MediaColumns.SIZE}>0")
                    hasVideoMaxSizeConfig {
                        args.add(it.toString())
                        builder.append(" AND ${MediaStore.MediaColumns.SIZE}<=?")
                    }
                }
                else -> {
                    //(media_type=? AND _size<=? OR media_type=? AND _size<=?) AND _size>0)
                    builder.append("(")
                    args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString())
                    builder.append("${MediaStore.Files.FileColumns.MEDIA_TYPE}=?")
                    hasImageMaxSizeConfig {
                        args.add(it.toString())
                        builder.append(" AND ${MediaStore.MediaColumns.SIZE}<=?")
                    }
                    args.add(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())
                    builder.append(" OR ${MediaStore.Files.FileColumns.MEDIA_TYPE}=?")
                    hasVideoMaxSizeConfig {
                        args.add(it.toString())
                        builder.append(" AND ${MediaStore.MediaColumns.SIZE}<=?")
                    }
                    builder.append(")")
                    builder.append(" AND ${MediaStore.MediaColumns.SIZE}>0")
                }
            }

            //AND mime_type IN (?,?) or mime_type NOT IN (?,?)
            hasMineTypeSet {
                builder.append(" AND ${MediaStore.MediaColumns.MIME_TYPE}")
                if (isFilterMimeTypeSet()) {
                    builder.append(" NOT IN ")
                } else {
                    builder.append(" IN ")
                }
                builder.append("(")
                it.forEachIndexed { index, mime ->
                    args.add(mime)
                    builder.append("?")
                    if (index < it.size - 1) {
                        builder.append(",")
                    }
                }
                builder.append(")")
            }
        }
    }
}