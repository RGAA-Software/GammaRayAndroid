package com.tc.client.ui.processes

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tc.client.R
import com.tc.client.Settings

class RunningProcessAdapter(private var context: Context, private var processes: MutableList<RunningProcess>)
    : RecyclerView.Adapter<RunningProcessAdapter.RunningProcessHolder>() {

    interface OnItemClickListener {
        fun onItemClicked(game: RunningProcess);
    }

    var itemClickListener: OnItemClickListener? = null

    class RunningProcessHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunningProcessHolder {
        val view = View.inflate(context, R.layout.item_running_process, null)
        return RunningProcessHolder(view)
    }

    override fun getItemCount(): Int {
        return processes.size
    }

    override fun onBindViewHolder(holder: RunningProcessHolder, position: Int) {
        val process = processes[position]
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClicked(process)
        }
        Glide.with(context)
            .load(Settings.getInstance().getApiBaseUrl() + "/icons/" + process.iconName)
            .into(holder.itemView.findViewById(R.id.id_process_icon))
        holder.itemView.findViewById<TextView>(R.id.id_process_id).text = process.pid.toString()

        if (process.exePath.isNotEmpty()) {
            val splitPaths = process.exePath.split("/")
            holder.itemView.findViewById<TextView>(R.id.id_process_name).text = splitPaths[splitPaths.size - 1]
        }
    }
}