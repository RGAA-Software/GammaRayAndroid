package com.tc.client.ui.steam

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.lid.lib.LabelImageView
import com.tc.client.R
import com.tc.client.Settings
import com.tc.client.steam.SteamGame

class SteamAppAdapter(private var context: Context, private var steamGames: MutableList<SteamGame>) :
    RecyclerView.Adapter<SteamAppAdapter.BookViewHolder>() {
    private val TAG = "Steam";

    interface OnItemClickListener {
        fun onItemClicked(game: SteamGame);
    }

    var itemClickListener: OnItemClickListener? = null

    class BookViewHolder(itemView: View) : ViewHolder(itemView) {
        val cover: LabelImageView = itemView.findViewById(R.id.game_cover);
        val appName: TextView = itemView.findViewById(R.id.id_app_name);
        val engineIndicator: ImageView = itemView.findViewById(R.id.id_engine);
        val presetIcon: ImageView = itemView.findViewById(R.id.id_status_on_icon);
        val gameRunningIndicator: CardView = itemView.findViewById(R.id.id_running_game_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = View.inflate(context, R.layout.item_steam_app, null);
        return BookViewHolder(view);
    }

    override fun getItemCount(): Int {
        return steamGames.size;
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val game = steamGames[position];
        holder.itemView.setOnClickListener {
            itemClickListener?.onItemClicked(game)
        }

        holder.appName.text = game.gameName
        holder.engineIndicator.visibility = View.GONE;
        holder.cover.isLabelVisual = false

        holder.gameRunningIndicator.visibility = if (game.gameTag == SteamGame.TAG_RUNNING) { View.VISIBLE } else{ View.GONE};

        if (position == 0) {
            holder.presetIcon.visibility = View.VISIBLE;
            holder.engineIndicator.visibility = View.GONE;
            holder.presetIcon.setImageDrawable(context.getDrawable(R.drawable.ic_windows));
            holder.cover.background = context.getDrawable(R.drawable.bg_item_gradient)
            Glide.with(context).load("").into(holder.cover);
        } else if (position == 1) {
            holder.presetIcon.visibility = View.VISIBLE;
            holder.engineIndicator.visibility = View.GONE;
            holder.presetIcon.setImageDrawable(context.getDrawable(R.drawable.ic_steam));
            holder.cover.background = context.getDrawable(R.drawable.bg_item_gradient)
            Glide.with(context).load("").into(holder.cover);
        } else {
            holder.presetIcon.visibility = View.GONE;
            val coverUrl: String = if (game.isSteamGame()) {
                Settings.getInstance().getApiBaseUrl() + "/steam/cache/" + game.gameId
            } else {
                Settings.getInstance().getApiBaseUrl() + "/" + game.coverName
            }
            Log.i(TAG, "cover url: $coverUrl")
            Glide.with(context).load(coverUrl).into(holder.cover);

            if (game.engine == "UNITY") {
                holder.cover.isLabelVisual = true
                holder.cover.labelText = game.engine
                //holder.engineIndicator.setImageDrawable(context.getDrawable(R.drawable.ic_unity))
            } else if (game.engine == "UE") {
                holder.cover.isLabelVisual = true
                holder.cover.labelText = game.engine
                //holder.engineIndicator.setImageDrawable(context.getDrawable(R.drawable.ic_ue))
            } else {
                //holder.cover.isLabelVisual = false
                //holder.engineIndicator.visibility = View.GONE;
            }
        }
    }

}