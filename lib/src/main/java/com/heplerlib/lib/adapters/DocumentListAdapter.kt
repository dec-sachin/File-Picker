package com.heplerlib.lib.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.heplerlib.lib.R
import com.heplerlib.lib.model.DocumentPojo
import kotlinx.android.synthetic.main.document_list_item_layout.view.*

class DocumentListAdapter(val context: Context, private val items: List<DocumentPojo>, private val itemClickListener: ItemClickListener) :
        RecyclerView.Adapter<DocumentListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflatedView = LayoutInflater.from(parent!!.context).inflate(R.layout.document_list_item_layout, parent, false)
        return ViewHolder(inflatedView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val documentPojo = items[position]
        holder!!.itemView.title.text = documentPojo.name
        holder!!.itemView.size.text = documentPojo.size
        holder!!.itemView.parent_view.setOnClickListener { itemClickListener?.onItemClick(documentPojo) }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface ItemClickListener {
        fun onItemClick(documentPojo: DocumentPojo)
    }

}