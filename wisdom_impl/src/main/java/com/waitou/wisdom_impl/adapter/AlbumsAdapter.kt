package com.waitou.wisdom_impl.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_lib.bean.Album
import com.waitou.wisdom_lib.config.WisdomConfig
import com.waitou.wisdom_lib.config.getMimeType
import com.waitou.wisdom_lib.config.isGif
import com.waitou.wisdom_lib.utils.getScreenImageResize
import kotlinx.android.synthetic.main.wis_item_albums.view.*

/**
 * auth aboom
 * date 2019-05-25
 */
class AlbumsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val albums = mutableListOf<Album>()
    var currentAlbumPos: Int = 0
    var function: Function1<Int, Unit>? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        return AlbumsViewHolder(LayoutInflater.from(p0.context).inflate(R.layout.wis_item_albums, p0, false))
                .apply { itemView.setOnClickListener { function?.invoke(adapterPosition) } }
    }

    override fun getItemCount(): Int = albums.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val album = albums[position]
        val screenImageResize = getScreenImageResize()
        WisdomConfig.getInstance().iImageEngine?.displayAlbum(
                holder.itemView.img,
                album.uri,
                screenImageResize,
                screenImageResize,
                isGif(getMimeType(album.path))
        )
        holder.itemView.name.text = album.albumName
        holder.itemView.num.text = album.count.toString()
        holder.itemView.check.visibility = if (holder.adapterPosition == currentAlbumPos) View.VISIBLE else View.GONE
    }

    private class AlbumsViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun replaceData(medias: List<Album>) {
        this.albums.clear()
        this.albums.addAll(medias)
        notifyDataSetChanged()
    }
}
