package com.jackl.example.adapter

import android.app.Activity
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.jackl.example.R

/**
 * @description:
 * @author: jackl
 * @date:  2021/12/15
 **/
class LogAdapter(recycler : RecyclerView,act : Activity) : RecyclerView.Adapter<LogHolder>() {

    private var recycler =recycler
    private var act =act
    var logs = arrayListOf<String>()

    fun addLog(content: String) {
        logs.add(content)
        if (Looper.myLooper() == Looper.getMainLooper()) {
            notifyDataSetChanged()
            recycler.smoothScrollToPosition(logs.size - 1)
        } else
            act.runOnUiThread {
                notifyDataSetChanged()
                recycler.smoothScrollToPosition(logs.size - 1)
            }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogHolder {
        var view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_log, parent, false)
        return LogHolder(view)
    }

    override fun getItemCount(): Int {
        return logs.size
    }

    override fun onBindViewHolder(holder: LogHolder, position: Int) {
        holder.textView?.text = logs[position]
    }
}

class LogHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var textView: TextView? = itemView.findViewById(R.id.content)
}