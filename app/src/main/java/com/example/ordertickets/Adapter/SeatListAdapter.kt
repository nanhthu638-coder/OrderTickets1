package com.example.ordertickets.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ordertickets.Models.Seat
import com.example.ordertickets.R
import com.example.ordertickets.databinding.SeatItemBinding

class SeatListAdapter(
    private val seatList: List<Seat>,
    private val context: Context,
    private val selectedSeat: SelectedSeat
) : RecyclerView.Adapter<SeatListAdapter.SeatViewHolder>() {
    private val selectedSeatName = ArrayList<String>()
    private val timers = HashMap<String, String>()

    class SeatViewHolder(val binding: SeatItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeatViewHolder {
        return SeatViewHolder(SeatItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    fun updateTimer(seatName: String, timeLeft: String?) {
        if (timeLeft == null) {
            timers.remove(seatName)
        } else {
            timers[seatName] = timeLeft
        }
        val position = seatList.indexOfFirst { it.name == seatName }
        if (position != -1) {
            notifyItemChanged(position, "timer_update")
        }
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int) {
        val seat = seatList[position]
        holder.binding.seat.text = seat.name

        // Hiển thị đồng hồ nếu có
        val timeLeft = timers[seat.name]
        if (timeLeft != null && seat.status == Seat.SeatStatus.SELECTED) {
            holder.binding.tvTimer.visibility = View.VISIBLE
            holder.binding.tvTimer.text = timeLeft
        } else {
            holder.binding.tvTimer.visibility = View.GONE
        }

        when (seat.status) {
            Seat.SeatStatus.AVAILABLE -> {
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_available)
                holder.binding.seat.setTextColor(context.getColor(R.color.white))
            }
            Seat.SeatStatus.SELECTED -> {
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_selected)
                holder.binding.seat.setTextColor(context.getColor(R.color.black))
            }
            Seat.SeatStatus.UNAVAILABLE -> {
                holder.binding.seat.setBackgroundResource(R.drawable.ic_seat_unavailable)
                holder.binding.seat.setTextColor(context.getColor(R.color.grey))
            }
        }

        holder.binding.seat.setOnClickListener {
            when (seat.status) {
                Seat.SeatStatus.AVAILABLE -> {
                    seat.status = Seat.SeatStatus.SELECTED
                    selectedSeatName.add(seat.name)
                    notifyItemChanged(position)
                }
                Seat.SeatStatus.SELECTED -> {
                    seat.status = Seat.SeatStatus.AVAILABLE
                    selectedSeatName.remove(seat.name)
                    timers.remove(seat.name)
                    notifyItemChanged(position)
                }
                else -> {}
            }
            val selected = selectedSeatName.joinToString(", ")
            selectedSeat.Return(selected, selectedSeatName.size)
            selectedSeat.onSeatClick(seat)
        }
    }

    override fun onBindViewHolder(holder: SeatViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains("timer_update")) {
            val seat = seatList[position]
            val timeLeft = timers[seat.name]
            if (timeLeft != null && seat.status == Seat.SeatStatus.SELECTED) {
                holder.binding.tvTimer.visibility = View.VISIBLE
                holder.binding.tvTimer.text = timeLeft
            } else {
                holder.binding.tvTimer.visibility = View.GONE
            }
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int = seatList.size

    interface SelectedSeat {
        fun Return(selectedName: String, num: Int)
        fun onSeatClick(seat: Seat)
    }
}
