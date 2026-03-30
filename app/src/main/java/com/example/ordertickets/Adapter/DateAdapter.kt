package com.example.ordertickets.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat

import androidx.recyclerview.widget.RecyclerView
import com.example.ordertickets.R
import com.example.ordertickets.databinding.ItemDateBinding


class DateAdapter(private val timeSlots: List<String>, private val onDateClick: (String) -> Unit) :
    RecyclerView.Adapter<DateAdapter.TimeViewHolder>() {
    private var selectedPosition = -1
    private var lastSelectedPosition = -1

    inner class TimeViewHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(date: String) {
            val dateParts = date.split("/")
            if (dateParts.size == 3) {
                binding.tvDayTxt.text = dateParts[0]
                binding.tvDateMonthTxt.text = dateParts[1] + " " + dateParts[2]

                if (selectedPosition == position) {
                   binding.mailLayout.setBackgroundResource(R.drawable.white_bg)
                    binding.tvDayTxt.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
                    binding.tvDateMonthTxt.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))

                } else {
                    binding.mailLayout.setBackgroundResource(R.drawable.light_black_bg)
                    binding.tvDayTxt.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                    binding.tvDateMonthTxt.setTextColor(ContextCompat.getColor(itemView.context, R.color.white))
                }

                binding.root.setOnClickListener {
                    val adapterPos = adapterPosition
                    if (adapterPos != RecyclerView.NO_POSITION) {
                        lastSelectedPosition = selectedPosition
                        selectedPosition = adapterPos

                        notifyItemChanged(lastSelectedPosition)
                        notifyItemChanged(selectedPosition)

                        // 🔥 gửi data ra ngoài
                        onDateClick(timeSlots[adapterPos])
                    }
                }
            }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DateAdapter.TimeViewHolder {
        return TimeViewHolder(
            ItemDateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: DateAdapter.TimeViewHolder, position: Int) {
        holder.bind(timeSlots[position])
    }

    override fun getItemCount(): Int = timeSlots.size

}
