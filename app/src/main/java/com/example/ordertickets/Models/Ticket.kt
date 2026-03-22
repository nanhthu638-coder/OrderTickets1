package com.example.ordertickets.Models

data class Ticket(
    val id: String = "",
    val filmTitle: String = "",
    val selectedSeats: String = "",
    val totalPrice: Double = 0.0,
    val date: String = "",
    val time: String = "",
    val userId: String = ""
)
