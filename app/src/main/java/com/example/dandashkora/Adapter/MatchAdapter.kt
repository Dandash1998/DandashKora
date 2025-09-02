package com.example.dandashkora.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dandashkora.R
import com.example.dandashkora.model.MatchItem

class MatchAdapter(
    private var matches: List<MatchItem>,
    private val onMatchClick: (MatchItem) -> Unit
) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {

    inner class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val team1Name: TextView = itemView.findViewById(R.id.team1Name)
        val team2Name: TextView = itemView.findViewById(R.id.team2Name)
        val team1Logo: ImageView = itemView.findViewById(R.id.team1Logo)
        val team2Logo: ImageView = itemView.findViewById(R.id.team2Logo)
        val matchTime: TextView = itemView.findViewById(R.id.matchTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        MatchViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_match, parent, false)
        )

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val match = matches[position]

        holder.team1Name.text = match.home_team.name
        holder.team2Name.text = match.away_team.name
        holder.matchTime.text = match.time

        val baseUrl = "https://football-apis-five.vercel.app"
        Glide.with(holder.itemView.context)
            .load(baseUrl + match.home_team.logo_url)
            .into(holder.team1Logo)
        Glide.with(holder.itemView.context)
            .load(baseUrl + match.away_team.logo_url)
            .into(holder.team2Logo)

        holder.itemView.setOnClickListener { onMatchClick(match) }
    }

    override fun getItemCount() = matches.size

    fun updateMatches(newMatches: List<MatchItem>) {
        matches = newMatches
        notifyDataSetChanged()
    }
}
