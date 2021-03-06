package com.waitou.wisdaoapp

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.blankj.utilcode.util.StringUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_item.view.*

/**
 * auth aboom
 * date 2019-06-12
 */
class MainAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val datas = mutableListOf<PathBean>()

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        val inflate = LayoutInflater.from(p0.context).inflate(R.layout.item_item, p0, false)
        return MediaViewHolder(inflate)
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val media = datas[p1]
        Glide.with(p0.itemView.image)
                .load(media.path)
                .apply(
                        RequestOptions()
                                .override(240, 240)
                )
                .into(p0.itemView.image)

        p0.itemView.size.text = media.size

    }

    private class MediaViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView)


    fun addData(medias: List<PathBean>) {
        this.datas.addAll(medias)
        notifyDataSetChanged()
    }
}
