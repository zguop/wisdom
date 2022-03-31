package com.waitou.wisdom_impl.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.recyclerview.widget.RecyclerView
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_impl.utils.formatSize
import com.waitou.wisdom_impl.view.CheckView
import com.waitou.wisdom_lib.bean.Media
import com.waitou.wisdom_lib.config.WisdomConfig
import com.waitou.wisdom_lib.utils.getScreenImageResize

/**
 * auth aboom
 * date 2019-05-28
 */
class MediasAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_CAPTURE: Int = 0x01
        private const val VIEW_TYPE_MEDIA: Int = 0x02

        private const val ITEM_PAYLOAD = 301
    }

    val medias = mutableListOf<Media>()
    val selectMedias = mutableListOf<Media>()

    var checkedListener: (() -> Unit)? = null
    var cameraClick: View.OnClickListener? = null
    var mediaClick: ((Int) -> Unit)? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(p0: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {
        VIEW_TYPE_CAPTURE -> CameraViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.wis_item_camera, p0, false)).apply {
            itemView.setOnClickListener { cameraClick?.onClick(it) }
        }
        else -> MediaViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.wis_item_media, p0, false)).apply {
            itemView.setOnClickListener {
                if (absoluteAdapterPosition != RecyclerView.NO_POSITION) {
                    mediaClick?.invoke(absoluteAdapterPosition)
                }
            }
            checkView.setOnCheckedChangeListener { _, _ ->
                mediaCheckedChange(medias[absoluteAdapterPosition], absoluteAdapterPosition)
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
            holder.checkView.isEnabled = !(selectMedias.size >= WisdomConfig.getInstance().maxSelectLimit && !holder.checkView.isChecked)
            holder.media.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(if (holder.checkView.isChecked)
                Color.argb(80, 0, 0, 0) else Color.TRANSPARENT, BlendModeCompat.SRC_ATOP)

            holder.size.text = media.size.formatSize()
            holder.gif.visibility = if (media.isGif()) View.VISIBLE else View.GONE
            holder.duration.visibility = if (media.isVideo()) {
                holder.duration.text = DateUtils.formatElapsedTime(media.duration / 1000)
                View.VISIBLE
            } else View.GONE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        } else {
            for (payload in payloads) {
                if (payload as? Int == ITEM_PAYLOAD && holder is MediaViewHolder) {
                    holder.checkView.setCheckedNum(selectMediaIndexOf(medias[position]))
                }
            }
        }
    }

    private fun selectMediaIndexOf(media: Media): Int {
        val indexOf = selectMedias.indexOf(media)
        return if (indexOf >= 0) indexOf + 1 else indexOf
    }

    fun mediaCheckedChange(media: Media, position: Int) {
        val checkedNumIndex = selectMediaIndexOf(media)
        if (checkedNumIndex > 0) {
            selectMedias.remove(media)
        } else {
            if (selectMedias.size >= WisdomConfig.getInstance().maxSelectLimit) {
                return
            }
            selectMedias.add(media)
        }
        notifyItemChanged(position, ITEM_PAYLOAD)
        checkedListener?.invoke()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun replaceMedias(medias: List<Media>) {
        this.medias.clear()
        this.medias.addAll(medias)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
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
