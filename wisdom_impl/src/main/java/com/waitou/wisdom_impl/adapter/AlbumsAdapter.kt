package com.waitou.wisdom_impl.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.waitou.wisdom_impl.R
import com.waitou.wisdom_lib.bean.Album
import com.waitou.wisdom_lib.config.WisdomConfig
import com.waitou.wisdom_lib.config.getMimeType
import com.waitou.wisdom_lib.config.isGif
import com.waitou.wisdom_lib.utils.getScreenImageResize

/**
 * auth aboom
 * date 2019-05-25
 */
class AlbumsAdapter : RecyclerView.Adapter<AlbumsAdapter.AlbumsViewHolder>() {

    val albums = mutableListOf<Album>()
    var currentAlbumPos: Int = 0
    var function: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AlbumsViewHolder {
        return AlbumsViewHolder(
                LayoutInflater.from(p0.context).inflate(R.layout.wis_item_albums, p0, false)
        ).apply {
            itemView.setOnClickListener { function?.invoke(adapterPosition) }
        }
    }

    override fun getItemCount(): Int = albums.size

    override fun onBindViewHolder(holder: AlbumsViewHolder, position: Int) {
        val album = albums[position]
        val screenImageResize = getScreenImageResize()
        WisdomConfig.getInstance().imageEngine?.displayAlbum(
                holder.img,
                album.uri,
                screenImageResize,
                screenImageResize,
                isGif(getMimeType(album.path))
        )
        holder.name.text = album.albumName
        holder.num.text = album.count.toString()
        holder.check.visibility = if (holder.adapterPosition == currentAlbumPos) View.VISIBLE else View.GONE
    }

    fun replaceData(medias: List<Album>) {
        this.albums.clear()
        this.albums.addAll(medias)
        notifyDataSetChanged()
    }

    class AlbumsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.img)
        val name: TextView = itemView.findViewById(R.id.name)
        val num: TextView = itemView.findViewById(R.id.num)
        val check: View = itemView.findViewById(R.id.check)
    }
}
