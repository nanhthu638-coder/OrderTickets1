package com.example.ordertickets.Adapter

import android.icu.text.DecimalFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ordertickets.Models.Ticket
import com.example.ordertickets.databinding.ViewholderTicketBinding
import com.google.firebase.database.FirebaseDatabase

class TicketAdapter(private val tickets: List<Ticket>) :
    RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    class TicketViewHolder(val binding: ViewholderTicketBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val binding = ViewholderTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TicketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        val df = DecimalFormat("#,###")
        holder.binding.tvTicketFilmTitle.text = ticket.filmTitle
        holder.binding.tvTicketSeats.text = "Ghế: ${ticket.selectedSeats}"
        holder.binding.tvTicketDateTime.text = "${ticket.date} - ${ticket.time}"
        holder.binding.tvTicketPrice.text = df.format(ticket.totalPrice) + " VND"

        holder.binding.tvCancelTicket.setOnClickListener {

            val database = FirebaseDatabase.getInstance().getReference("Tickets")

            database.child(ticket.id).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(holder.itemView.context, "Huỷ vé thành công", Toast.LENGTH_SHORT).show()

                    (tickets as MutableList).removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, tickets.size)
                }
                .addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "Huỷ vé thất bại", Toast.LENGTH_SHORT).show()
                }
        }
    }

    override fun getItemCount() = tickets.size
}