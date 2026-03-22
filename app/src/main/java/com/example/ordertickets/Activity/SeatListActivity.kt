package com.example.ordertickets.Activity

import android.icu.text.DecimalFormat
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ordertickets.Adapter.DateAdapter
import com.example.ordertickets.Adapter.SeatListAdapter
import com.example.ordertickets.Adapter.TimeAdapter
import com.example.ordertickets.Models.Film
import com.example.ordertickets.Models.Seat
import com.example.ordertickets.R
import com.example.ordertickets.databinding.ActivitySeatListBinding
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import android.content.Intent
import android.widget.Toast
class SeatListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySeatListBinding
    private lateinit var film: Film
    private var price: Double = 0.0
    private var number: Int=0
    private var selectedSeatNames=""
    private var selectedDate=""
    private var selectedTime=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivitySeatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentExtra()
        setVariable()
        initSeatsList()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
    private fun initSeatsList() {
        val gridLayoutManager= GridLayoutManager(this, 7)
        gridLayoutManager.spanSizeLookup=object: GridLayoutManager.SpanSizeLookup(){
            override fun getSpanSize(position: Int): Int {
                return if(position%7==3) 1 else 1
            }
        }
        binding.rvSeats.layoutManager=gridLayoutManager
        val seatList= mutableListOf<Seat>()
        val numberSeats=81
        for(i in 0 until numberSeats) {
            val SeatName=""
            val SeatStatus = if(i==2|| i==20|| i==33|| i==41|| i==50|| i==72|| i==73)Seat.SeatStatus.UNAVAILABLE
            else Seat.SeatStatus.AVAILABLE
            seatList.add(Seat(SeatStatus,SeatName))
        }

        val SeatAdapter= SeatListAdapter(seatList,this,object :SeatListAdapter.SelectedSeat{
            override fun Return(selectedName: String, num: Int) {
                binding.tvSeatSelected.text="$num Seat Selected"
                var df= DecimalFormat("#.##")
                price=df.format(num*film.Price).toDouble()
                number = num
                selectedSeatNames=selectedName

                binding.tvPriceTxt.text="$$price"
            }

        })
        binding.btnDownloadTicket.setOnClickListener {
            if (number > 0) {
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("filmTitle", film.Title)
                intent.putExtra("selectedSeats", selectedSeatNames)
                intent.putExtra("totalPrice", price)
                // Lưu ý: Bạn cần logic để lấy ngày/giờ đang chọn từ Adapter
                // Tạm thời truyền mẫu:
                intent.putExtra("date", "20/10/2023")
                intent.putExtra("time", "10:00 AM")

                startActivity(intent)
            } else {
                Toast.makeText(this, "Vui lòng chọn ghế trước khi thanh toán", Toast.LENGTH_SHORT).show()
            }
        }
        binding.rvSeats.adapter=SeatAdapter
        binding.rvSeats.isNestedScrollingEnabled=false

        binding.rvTime.layoutManager= LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        binding.rvTime.adapter= TimeAdapter(generateTimeSlots())

        binding.rvDate.layoutManager= LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        binding.rvDate.adapter= DateAdapter(generateDate())
    }
    private fun setVariable(){
        binding.imgVBackButton.setOnClickListener { finish() }
    }

    private fun getIntentExtra() {
        film=intent.getParcelableExtra("film")!!
    }
    private fun generateTimeSlots(): List<String>{
        val timeSlots= mutableListOf<String>()
        val formatter= DateTimeFormatter.ofPattern("hh:mm a")
        for(i in 0 until 24 step 2){
            val time= LocalTime.of(i,0).format(formatter)
            timeSlots.add(time)
        }
        return timeSlots
    }
    private fun generateDate(): List<String>{
        val dates= mutableListOf<String>()
        val today= LocalDate.now()
        val formatter= DateTimeFormatter.ofPattern("dd/MM/yyyy")
        for(i in 0 until 7){
            dates.add(today.plusDays(i.toLong()).format(formatter))
        }
        return dates
    }
}