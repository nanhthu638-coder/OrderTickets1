package com.example.ordertickets.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ordertickets.databinding.ViewholderCategoryBinding

class CategoryEachFilmAdapter(private val items: List<String>):
    RecyclerView.Adapter<CategoryEachFilmAdapter.ViewHolder>() {
    class ViewHolder(val biding: ViewholderCategoryBinding): RecyclerView.ViewHolder(biding.root)
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryEachFilmAdapter.ViewHolder {
       val biding=ViewholderCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(biding)
    }

    override fun onBindViewHolder(holder: CategoryEachFilmAdapter.ViewHolder, position: Int) {
        holder.biding.tvTitleTxt2.text = items[position]
    }

    override fun getItemCount(): Int = items.size
}