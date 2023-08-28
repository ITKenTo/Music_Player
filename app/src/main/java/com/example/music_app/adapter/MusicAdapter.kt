package com.example.music_app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.music_app.databinding.ItemMusicBinding
import com.example.music_app.model.MusicFileModel

class MusicAdapter(private val listMusic: List<MusicFileModel>, val onClick:(MusicFileModel) ->Unit):RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

    inner class ViewHolder( val binding: ItemMusicBinding):RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       return ViewHolder(ItemMusicBinding.inflate(LayoutInflater.from(parent.context), parent,false))
    }

    override fun getItemCount(): Int {
        return listMusic.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val musicFileModel= listMusic[position]
        holder.binding.apply {
            tvTitle.text=musicFileModel.title
            tvArtist.text=musicFileModel.artist
        }

        holder.itemView.setOnClickListener {
            onClick.invoke(listMusic[position])
        }
    }
}