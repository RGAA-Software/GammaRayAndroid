package com.tc.client.ui.server

import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.tc.client.R
import com.tc.client.db.DBServer
import com.tc.client.ui.base.OnListItemListener

class ServerAdapter(private var context: Context, private var servers: MutableList<DBServer>) :
    RecyclerView.Adapter<ServerAdapter.BookViewHolder>() {

    private val TAG = "Steam";

    private var itemClickListener: OnListItemListener<DBServer>? = null

    class BookViewHolder(itemView: View) : ViewHolder(itemView) {
        val cover: ImageView = itemView.findViewById(R.id.effect_cover);
        val appName: TextView = itemView.findViewById(R.id.id_app_name);
        val statusOnIcon: LottieAnimationView = itemView.findViewById(R.id.id_status_on_icon);
        val statusOffIcon: LottieAnimationView = itemView.findViewById(R.id.id_status_off_icon);
        val connectScreen: ImageView = itemView.findViewById(R.id.connect_screen);
        val searching: ImageView = itemView.findViewById(R.id.searching);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = View.inflate(context, R.layout.item_machine, null);
        val mgr = context.assets;
        val tf = Typeface.createFromAsset(mgr, "fonts/matrix.ttf");
        view.findViewById<TextView>(R.id.id_app_name).typeface = tf;

        return BookViewHolder(view);
    }

    override fun getItemCount(): Int {
        return servers.size;
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val server = servers[position];
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClicked(position, server)
        }

        holder.connectScreen.visibility = View.VISIBLE;
        holder.searching.visibility = View.GONE;

        holder.appName.text = server.serverId;
        val iconUrl = "http://${server.serverIp}:${server.httpServerPort}/resources/${server.iconIndex}.png"
        Log.i(TAG, "iconurl: $iconUrl")
        Glide.with(context).load(iconUrl).into(holder.connectScreen);
        if (server.available) {
            holder.statusOnIcon.visibility = View.VISIBLE;
            holder.statusOffIcon.visibility = View.GONE
            holder.appName.setTextColor(context.getColor(R.color.colorPrimary));
        } else {
            holder.statusOnIcon.visibility = View.GONE;
            holder.statusOffIcon.visibility = View.VISIBLE
            holder.appName.setTextColor(context.getColor(R.color.dark));
        }
    }

    fun setOnItemClickListener(listener: OnListItemListener<DBServer>) {
        itemClickListener = listener
    }

}