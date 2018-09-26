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
import com.heplerlib.lib.screens.AllImagesScreen
import com.heplerlib.lib.screens.Gallery
import kotlinx.android.synthetic.main.all_images_list_item_layout.view.*
import java.io.File

class AllImagesAdapter(private val context: Context, private val items: List<ImageFile>, private val itemClickListener: ItemClickListener,
                       private var totalSelected: Int) : RecyclerView.Adapter<AllImagesAdapter.ViewHolder>() {

    private var maxSelection = 0
    private var selectionFlag = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent!!.context).inflate(R.layout.all_images_list_item_layout, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageFile = items[position]
        Glide.with(context).load(File(imageFile.path)).apply(RequestOptions().override(200)).into(holder!!.itemView.image)
        if (imageFile.checked == 1)
            holder!!.itemView.checked.visibility = View.VISIBLE
        else
            holder!!.itemView.checked.visibility = View.GONE

        holder!!.itemView.image.setOnClickListener {
            if (Gallery.selectionType == Gallery.SINGLE) {
                itemClickListener?.onItemClick(imageFile)
            } else {
                if (selectionFlag) {
                    if (imageFile.checked == 1) {
                        imageFile.checked = 0
                        maxSelection--
                        totalSelected--
                        if (maxSelection == 0)
                            selectionFlag = false
                    } else {
                        if (totalSelected < Gallery.maxSelection) {
                            imageFile.checked = 1
                            maxSelection++
                            totalSelected++
                        }
                    }
                    notifyItemChanged(position)
                    (context as AllImagesScreen).updateMenu(maxSelection)
                } else {
                    itemClickListener?.onItemClick(imageFile)
                }
            }
        }

        if (Gallery.selectionType != Gallery.SINGLE) {
            holder!!.itemView.image.setOnLongClickListener {
                selectionFlag = true
                return@setOnLongClickListener false
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface ItemClickListener {
        fun onItemClick(imageFile: ImageFile)
    }

}