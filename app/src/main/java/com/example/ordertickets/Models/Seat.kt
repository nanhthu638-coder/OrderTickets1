package com.example.ordertickets.Models

data class Seat(var status: SeatStatus,var name: String){
    enum class SeatStatus{
        AVAILABLE,
        SELECTED,
        UNAVAILABLE
    }
}
