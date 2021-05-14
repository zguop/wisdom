package com.waitou.wisdom_impl.adapter

import android.graphics.Color
import android.graphics.PorterDuff
import android.support.v7.widget.RecyclerView
import android.text.format.DateUtils
import android.text.format.Formatter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_impl.view.CheckView
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.WisdomConfig
import com.waitou.wisdom_lib.utils.getScreenImageResize
import com.waitou.wisdom_lib.utils.isSingleImage

/**
 * auth aboom
 * date 2019-05-28
 */
class MediasAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CAPTURE: Int = 0x01
        private const val VIEW_TYPE_MEDIA: Int = 0x02
    }

    private val medias = mutableListOf<Media>()
    val selectMedias = mutableListOf<Media>()

    var checkedListener: OnCheckedChangedListener? = null
    var cameraClick: View.OnClickListener? = null
    var mediaClick: ((Media, Int, View) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        VIEW_TYPE_CAPTURE -> CameraViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.wis_item_camera, p0, false)).apply {
            itemView.setOnClickListener { cameraClick?.onClick(it) }
        }
        else -> MediaViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.wis_item_media, p0, false)).apply {
            itemView.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    mediaClick?.invoke(medias[adapterPosition], adapterPosition, it)
                }
            }
            checkView.setOnCheckedChangeListener { _, _ ->
                mediaCheckedChange(medias[adapterPosition])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (medias[position].mediaId == Media.ITEM_ID_CAPTURE) VIEW_TYPE_CAPTURE else VIEW_TYPE_MEDIA
    }

    override fun getItemCount(): Int {
        return medias.size
    }

    override fun getItemId(position: Int): Long {
        return medias[position].hashCode().toLong()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val media = medias[position]
        if (holder is CameraViewHolder) {
            holder.cameraText.text = holder.itemView.context.getString(R.string.wis_take)
        } else if (holder is MediaViewHolder) {
            WisdomConfig.getInstance().imageEngine?.displayThumbnail(
                    holder.media, media.uri, getScreenImageResize(), getScreenImageResize(), media.isGif()
            )
            holder.checkView.setCheckedNum(selectMediaIndexOf(media))
            holder.checkView.visibility = if (isSingleImage()) View.GONE else View.VISIBLE
            holder.media.setColorFilter(
                    if (holder.checkView.isChecked) Color.argb(80, 0, 0, 0) else Color.TRANSPARENT,
                    PorterDuff.Mode.SRC_ATOP
            )
            holder.size.text = Formatter.formatShortFileSize(holder.itemView.context, media.size)
            holder.gif.visibility = if (media.isGif()) View.VISIBLE else View.GONE
            holder.duration.visibility = if (media.isVideo()) {
                holder.duration.text = DateUtils.formatElapsedTime(media.duration / 1000)
                View.VISIBLE
            } else View.GONE
        }
    }

    private fun mediaCheckedChange(media: Media) {
        val checkedNumIndex = selectMediaIndexOf(media)
        if (checkedNumIndex > 0) {
            selectMedias.remove(media)
        } else {
            if (selectMedias.size >= WisdomConfig.getInstance().maxSelectLimit) {
                return
            }
            selectMedias.add(media)
        }
        notifyDataSetChanged()
        checkedListener?.onChange()
    }

    interface OnCheckedChangedListener {
        fun onChange()
    }

    private fun selectMediaIndexOf(media: Media): Int {
        val indexOf = selectMedias.indexOf(media)
        return if (indexOf >= 0) indexOf + 1 else indexOf
    }

    fun replaceMedias(medias: List<Media>) {
        this.medias.clear()
        this.medias.addAll(medias)
        notifyDataSetChanged()
    }

    fun replaceSelectMedias(medias: List<Media>) {
        this.selectMedias.clear()
        this.selectMedias.addAll(medias)
        notifyDataSetChanged()
    }
    class CameraViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cameraText: TextView = itemView.findViewById(R.id.cameraText)
    }

    class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkView: CheckView = itemView.findViewById(R.id.checkView)
        val media: ImageView = itemView.findViewById(R.id.media)
        val size: TextView = itemView.findViewById(R.id.size)
        val gif: View = itemView.findViewById(R.id.gif)
        val duration: TextView = itemView.findViewById(R.id.duration)
    }
}
