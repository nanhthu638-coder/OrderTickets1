package com.example.ordertickets.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.ordertickets.R
import com.example.ordertickets.databinding.ItemTimeBinding

class TimeAdapter(
    private val timeSlots: List<String>,
    private val onTimeSelected: (String) -> Unit
) : RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {
    private var selectedPosition = -1
    private var lastSelectedPosition = -1

    inner class TimeViewHolder(private val binding: ItemTimeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(time: String, position: Int) {
            binding.tvTime.text = time
            if (selectedPosition == position) {
                binding.tvTime.setBackgroundResource(R.drawable.white_bg)
                binding.tvTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            } else {
                binding.tvTime.setBackgroundResource(R.drawable.light_black_bg)
                binding.tvTime.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
            }
            binding.root.setOnClickListener {
                if (position != RecyclerView.NO_POSITION) {
                    lastSelectedPosition = selectedPosition
                    selectedPosition = position
                    notifyItemChanged(lastSelectedPosition)
                    notifyItemChanged(selectedPosition)
                    onTimeSelected(time)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        return TimeViewHolder(
            ItemTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.bind(timeSlots[position], position)
    }

    override fun getItemCount(): Int = timeSlots.size
}
