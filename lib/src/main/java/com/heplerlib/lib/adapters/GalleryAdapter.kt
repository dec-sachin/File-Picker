package com.heplerlib.lib.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.heplerlib.lib.model.ImageFile
import com.heplerlib.lib.R
import kotlinx.android.synthetic.main.gallery_list_item_layout.view.*
import java.io.File

class GalleryAdapter(val context: Context, private val items: List<ImageFile>, private val itemClickListener: ItemClickListener) :
        RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent!!.context).inflate(R.layout.gallery_list_item_layout, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageFile = items[position]
        if (imageFile.isVideo == 1)
            holder!!.itemView.symbol.setImageResource(R.drawable.ic_video_symbol)
        else
            holder!!.itemView.symbol.setImageResource(R.drawable.ic_image_symbol)
        Glide.with(context).load(File(imageFile.path)).apply(RequestOptions().override(180)).into(holder!!.itemView.image)
        holder!!.itemView.name.text = imageFile.groupName
        holder!!.itemView.count.text = imageFile.counter.toString()
        if (imageFile.selectionCount > 0) {
            holder!!.itemView.selection_count.text = String.format("%02d", imageFile.selectionCount)
            holder!!.itemView.selection_count.visibility = View.VISIBLE
        } else
            holder!!.itemView.selection_count.visibility = View.GONE
        holder!!.itemView.image.setOnClickListener { itemClickListener?.onItemClick(imageFile) }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface ItemClickListener {
        fun onItemClick(imageFile: ImageFile)
    }

}