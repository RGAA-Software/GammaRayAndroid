package com.tc.client.ui.steam

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.tc.client.FrameRenderActivity
import com.tc.client.R
import com.tc.client.Settings
import com.tc.client.steam.SteamApp

class SteamAppAdapter(private var context: Context, private var apps: MutableList<SteamApp>) :
    RecyclerView.Adapter<SteamAppAdapter.BookViewHolder>() {
    private val TAG = "Steam";

    class BookViewHolder(itemView: View) : ViewHolder(itemView) {
        val cover: ImageView = itemView.findViewById(R.id.book_cover);
        val appName: TextView = itemView.findViewById(R.id.id_app_name);
        val engineIndicator: ImageView = itemView.findViewById(R.id.id_engine);
        val presetIcon: ImageView = itemView.findViewById(R.id.id_preset_icon);
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
//            intent.putExtra("ip", "192.168.31.5");
            intent.putExtra("ip", "10.0.0.16");
            intent.putExtra("port", 9002);
            context.startActivity(intent)
        }

        holder.appName.text = app.appName

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
            val coverUrl = Settings.getInstance().apiBaseUrl + "/cache/" + app.coverName;
            Glide.with(context).load(coverUrl).into(holder.cover);

            holder.engineIndicator.visibility = View.VISIBLE;
            if (app.engine == "UNITY") {
                holder.engineIndicator.setImageDrawable(context.getDrawable(R.drawable.ic_unity))
            } else if (app.engine == "UE") {
                holder.engineIndicator.setImageDrawable(context.getDrawable(R.drawable.ic_ue))
            } else {
                holder.engineIndicator.visibility = View.GONE;
            }
        }
    }

}