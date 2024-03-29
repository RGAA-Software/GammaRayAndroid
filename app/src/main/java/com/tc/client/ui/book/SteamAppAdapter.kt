package com.tc.client.ui.book

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.tc.client.R
import com.tc.client.Settings
import com.tc.client.steam.SteamApp

class SteamAppAdapter(private var context: Context, private var apps: MutableList<SteamApp>) :
    RecyclerView.Adapter<SteamAppAdapter.BookViewHolder>() {
    private val TAG = "Steam";

    class BookViewHolder(itemView: View) : ViewHolder(itemView) {
        val cover: ImageView = itemView.findViewById(R.id.book_cover);
        val appName: TextView = itemView.findViewById(R.id.id_app_name);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = View.inflate(context, R.layout.item_book, null);
        return BookViewHolder(view);
    }

    override fun getItemCount(): Int {
        return apps.size;
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val app = apps[position];
        holder.itemView.setOnClickListener {
            Toast.makeText(context, "index:$position", Toast.LENGTH_SHORT).show();
        }
        val coverUrl = Settings.getInstance().apiBaseUrl + "/cache/" + app.coverName;
        Glide.with(context).load(coverUrl).into(holder.cover);

        holder.appName.text = app.appName
    }

}