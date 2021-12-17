package com.jackl.example.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jackl.example.model.Data
import com.jackl.example.R

/**
 * @description:
 * @author: jackl
 * @date:  2021/12/15
 **/
class PreviewAdapter(recycler : RecyclerView) : RecyclerView.Adapter<PreviewHolder>() {

    private var recycler =recycler
    var datas = arrayListOf<Data>()

    fun addData(data: Data) {
        datas.add(data)
        notifyDataSetChanged()
        recycler.smoothScrollToPosition(datas.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreviewHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_preview, parent, false)
        return PreviewHolder(view)
    }

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: PreviewHolder, position: Int) {
        holder.imageView?.let {
            Glide.with(it).load(datas[position].avatar).into(it)
        }
    }
}

class PreviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var imageView: ImageView? = itemView.findViewById(R.id.preview)
}