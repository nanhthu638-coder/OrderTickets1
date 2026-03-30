package com.example.ordertickets.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.ordertickets.Activity.FilmDetailActivity
import com.example.ordertickets.Models.Film
import com.example.ordertickets.databinding.ViewholderFilmBinding



class FilmListAdapter(private var items:ArrayList<Film>):
    RecyclerView.Adapter<FilmListAdapter.Viewholder>() {
        private var context: Context?=null
    inner class Viewholder (private val binding: ViewholderFilmBinding):
    RecyclerView.ViewHolder(binding.root){
        fun bind(film: Film){
            binding.tvFilmNametxt.text=film.Title
            val requestOptions= RequestOptions().transform(CenterCrop(), RoundedCorners(30))
            Glide.with(context!!)
                .load(film.Poster)
                .apply(requestOptions)
                .into(binding.imgVPic)
            binding.root.setOnClickListener {
             val intent = Intent(context, FilmDetailActivity::class.java)
                intent.putExtra("object", film)
                context!!.startActivity(intent)
            }
        }
    }

    fun updateList(newList: List<Film>) {
        items = newList.toMutableList() as ArrayList<Film>
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FilmListAdapter.Viewholder {
        context=parent.context
        val binding= ViewholderFilmBinding.inflate(LayoutInflater.from(context),parent,false)
        return Viewholder(binding)
    }

    override fun onBindViewHolder(holder: FilmListAdapter.Viewholder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int =items.size
}