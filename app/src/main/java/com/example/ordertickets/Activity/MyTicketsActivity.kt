package com.example.ordertickets.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ordertickets.Adapter.TicketAdapter
import com.example.ordertickets.Models.Ticket
import com.example.ordertickets.databinding.ActivityMyTicketsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MyTicketsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyTicketsBinding
    private lateinit var ticketAdapter: TicketAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyTicketsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.imgBack.setOnClickListener { finish() }

        setupRecyclerView()
        loadTicketsFromFirebase()
    }

    private fun setupRecyclerView() {
        binding.rvMyTickets.layoutManager = LinearLayoutManager(this)
    }

    private fun loadTicketsFromFirebase() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        binding.progressBar.visibility = View.VISIBLE

        val ref = FirebaseDatabase.getInstance().getReference("Tickets")
        ref.orderByChild("userId").equalTo(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.progressBar.visibility = View.GONE
                val list = mutableListOf<Ticket>()
                for (ds in snapshot.children) {
                    val ticket = ds.getValue(Ticket::class.java)
                    if (ticket != null) list.add(ticket)
                }

                if (list.isEmpty()) {
                    binding.tvEmpty.visibility = View.VISIBLE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                    ticketAdapter = TicketAdapter(list)
                    binding.rvMyTickets.adapter = ticketAdapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                binding.progressBar.visibility = View.GONE
            }
        })
    }
}
