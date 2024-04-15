package com.tc.client.ui.steam

import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.lid.lib.LabelImageView
import com.tc.client.FrameRenderActivity
import com.tc.client.R
import com.tc.client.Settings
import com.tc.client.steam.SteamApp

class SteamAppAdapter(private var context: Context, private var apps: MutableList<SteamApp>) :
    RecyclerView.Adapter<SteamAppAdapter.BookViewHolder>() {
    private val TAG = "Steam";

    class BookViewHolder(itemView: View) : ViewHolder(itemView) {
        val cover: LabelImageView = itemView.findViewById(R.id.game_cover);
        val appName: TextView = itemView.findViewById(R.id.id_app_name);
        val engineIndicator: ImageView = itemView.findViewById(R.id.id_engine);
        val presetIcon: ImageView = itemView.findViewById(R.id.id_status_on_icon);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = View.inflate(context, R.layout.item_steam_app, null);
        return BookViewHolder(view);
    }

    override fun getItemCount(): Int {
        return apps.size;
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val app = apps[position];
        holder.itemView.setOnClickListener {
            Toast.makeText(context, "index:$position", Toast.LENGTH_SHORT).show();
            val intent = Intent(context, FrameRenderActivity::class.java);
            val server = Settings.getInstance().currentServer
            if (!server.available || TextUtils.isEmpty(server.serverIp)) {
                Toast.makeText(context, "Server has not connected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener;
            }

            intent.putExtra("ip", server.serverIp);
            intent.putExtra("port", server.streamWsPort);
            context.startActivity(intent)
        }

        holder.appName.text = app.appName
        holder.engineIndicator.visibility = View.GONE;
        holder.cover.isLabelVisual = false

        if (position == 0) {
            holder.presetIcon.visibility = View.VISIBLE;
            holder.engineIndicator.visibility = View.GONE;
            holder.presetIcon.setImageDrawable(context.getDrawable(R.drawable.ic_windows));
            Glide.with(context).load("").into(holder.cover);
        } else if (position == 1) {
            holder.presetIcon.visibility = View.VISIBLE;
            holder.engineIndicator.visibility = View.GONE;
            holder.presetIcon.setImageDrawable(context.getDrawable(R.drawable.ic_steam));
            Glide.with(context).load("").into(holder.cover);
        } else {
            holder.presetIcon.visibility = View.GONE;
            val coverUrl = Settings.getInstance().getApiBaseUrl() + "/cache/" + app.coverName;
            Glide.with(context).load(coverUrl).into(holder.cover);

            if (app.engine == "UNITY") {
                holder.cover.isLabelVisual = true
                holder.cover.labelText = app.engine
                //holder.engineIndicator.setImageDrawable(context.getDrawable(R.drawable.ic_unity))
            } else if (app.engine == "UE") {
                holder.cover.isLabelVisual = true
                holder.cover.labelText = app.engine
                //holder.engineIndicator.setImageDrawable(context.getDrawable(R.drawable.ic_ue))
            } else {
                //holder.cover.isLabelVisual = false
                //holder.engineIndicator.visibility = View.GONE;
            }
        }
    }

}