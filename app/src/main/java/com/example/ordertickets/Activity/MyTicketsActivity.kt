package com.example.ordertickets.Activity

import com.example.ordertickets.Models.Ticket
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlin.collections.getValue

class MyTicketsActivity {
    // Trong MyTicketsActivity.kt
    private fun loadTickets() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ref =
            FirebaseDatabase.getInstance().getReference("Tickets")
        // Lọc vé theo ID người dùng hiện tại
        ref.orderByChild("userId").equalTo(userId).addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val ticketList = mutableListOf<Ticket>()
                for (ds in snapshot.children) {
                    val ticket = ds.getValue(Ticket::class.java)
                    if (ticket != null) ticketList.add(ticket)
                }
                // Gán ticketList vào TicketAdapter và hiển thị lên RecyclerView
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
}