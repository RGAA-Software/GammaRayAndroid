package com.tc.client.ui.machine

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.tc.client.R
import com.tc.client.activity.QRCodeScanActivity
import com.tc.client.db.DBServer

class MachineAdapter(private var context: Context, private var apps: MutableList<DBServer>) :
    RecyclerView.Adapter<MachineAdapter.BookViewHolder>() {
    private val TAG = "Steam";

    class BookViewHolder(itemView: View) : ViewHolder(itemView) {
        val cover: ImageView = itemView.findViewById(R.id.book_cover);
        val appName: TextView = itemView.findViewById(R.id.id_app_name);
        val presetIcon: ImageView = itemView.findViewById(R.id.id_preset_icon);
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
        return apps.size;
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val app = apps[position];
        holder.itemView.setOnClickListener {

        }

        holder.appName.text = "PC:${app.serverId}";
        val iconUrl = "http://${app.serverIp}:${app.httpServerPort}/res/${app.iconIndex}.png"
        Log.i(TAG, "icon url: $iconUrl")
        Glide.with(context).load(iconUrl).into(holder.connectScreen);

//        if (position == 0) {
//            holder.presetIcon.visibility = View.GONE;
//            holder.connectScreen.visibility = View.GONE;
//            holder.searching.visibility = View.VISIBLE;
//        } else {
//
//        }

        holder.presetIcon.visibility = View.VISIBLE;
        holder.connectScreen.visibility = View.VISIBLE;
        holder.searching.visibility = View.GONE;
    }

}