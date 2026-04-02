package com.example.ordertickets.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ordertickets.Models.Ticket
import com.example.ordertickets.databinding.ViewholderTicketBinding
import java.text.NumberFormat
import java.util.Locale
fun Double.toVND(): String {
    val formatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("vi", "VN"))// Nhân với 1000 để biến 240.0 thành 240.000
    return formatter.format(this * 1000)
}
class TicketAdapter(private val tickets: List<Ticket>) :
    RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    class TicketViewHolder(val binding: ViewholderTicketBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val binding = ViewholderTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TicketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.binding.tvTicketFilmTitle.text = ticket.filmTitle
        holder.binding.tvTicketSeats.text = "Ghế: ${ticket.selectedSeats}"
        holder.binding.tvTicketDateTime.text = "${ticket.date} - ${ticket.time}"
        val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
        holder.binding.tvTicketPrice.text = formatter.format(ticket.totalPrice)
    }

    override fun getItemCount() = tickets.size
}