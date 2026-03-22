package com.example.ordertickets.Activity

import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.os.CountDownTimer
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ordertickets.Adapter.DateAdapter
import com.example.ordertickets.Adapter.SeatListAdapter
import com.example.ordertickets.Adapter.TimeAdapter
import com.example.ordertickets.Models.Film
import com.example.ordertickets.Models.Seat
import com.example.ordertickets.databinding.ActivitySeatListBinding
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class SeatListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySeatListBinding
    private lateinit var film: Film
    private var price: Double = 0.0
    private var number: Int = 0
    private var selectedSeatNames = ""
    private val seatTimers = HashMap<String, CountDownTimer>()
    private val HOLD_TIME = 40 * 1000L // Đã chỉnh xuống 40 giây

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatListBinding.inflate(layoutInflater)
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
        val gridLayoutManager = GridLayoutManager(this, 7)
        binding.rvSeats.layoutManager = gridLayoutManager

        val seatList = mutableListOf<Seat>()
        for (i in 0 until 81) {
            val rowIndex = i / 7
            val colIndex = i % 7
            val rowChar = ('A' + rowIndex)
            val seatName = "$rowChar${colIndex + 1}"

            val seatStatus =
                if (i == 2 || i == 20 || i == 33 || i == 41 || i == 50 || i == 72 || i == 73)
                    Seat.SeatStatus.UNAVAILABLE
                else
                    Seat.SeatStatus.AVAILABLE

            seatList.add(Seat(seatStatus, seatName))
        }

        val seatAdapter = SeatListAdapter(seatList, this, object : SeatListAdapter.SelectedSeat {
            override fun Return(selectedName: String, num: Int) {
                binding.tvSeatSelected.text = "$num Seat Selected"
                val df = DecimalFormat("#.##")
                price = df.format(num * film.Price).toDouble()
                number = num
                selectedSeatNames = selectedName
                binding.tvPriceTxt.text = "$$price"
            }

            override fun onSeatClick(seat: Seat) {
                if (seat.status == Seat.SeatStatus.SELECTED) {
                    startTimer(seat)
                } else {
                    cancelTimer(seat)
                }
            }
        })

        binding.rvSeats.adapter = seatAdapter
        binding.rvSeats.isNestedScrollingEnabled = false

        binding.rvTime.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTime.adapter = TimeAdapter(generateTimeSlots())

        binding.rvDate.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvDate.adapter = DateAdapter(generateDate())
    }

    private fun startTimer(seat: Seat) {
        cancelTimer(seat)
        val timer = object : CountDownTimer(HOLD_TIME, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Cập nhật số giây còn lại lên giao diện ghế
                val secondsLeft = (millisUntilFinished / 1000).toString()
                (binding.rvSeats.adapter as? SeatListAdapter)?.updateTimer(seat.name, secondsLeft)
            }

            override fun onFinish() {
                if (seat.status == Seat.SeatStatus.SELECTED) {
                    seat.status = Seat.SeatStatus.AVAILABLE
                    (binding.rvSeats.adapter as? SeatListAdapter)?.updateTimer(seat.name, null)

                    val list = selectedSeatNames.split(", ")
                        .filter { it != seat.name && it.isNotEmpty() }
                        .toMutableList()
                    selectedSeatNames = list.joinToString(", ")
                    number = list.size
                    binding.tvSeatSelected.text = "$number Seat Selected"
                    val df = DecimalFormat("#.##")
                    price = df.format(number * film.Price).toDouble()
                    binding.tvPriceTxt.text = "$$price"

                    binding.rvSeats.adapter?.notifyDataSetChanged()
                    Toast.makeText(this@SeatListActivity, "Hết thời gian giữ ghế ${seat.name}", Toast.LENGTH_SHORT).show()
                    seatTimers.remove(seat.name)
                }
            }
        }
        timer.start()
        seatTimers[seat.name] = timer
    }

    private fun cancelTimer(seat: Seat) {
        seatTimers[seat.name]?.cancel()
        seatTimers.remove(seat.name)
        (binding.rvSeats.adapter as? SeatListAdapter)?.updateTimer(seat.name, null)
    }

    private fun setVariable() {
        binding.imgVBackButton.setOnClickListener { finish() }

        binding.btnDownloadTicket.setOnClickListener {
            if (number > 0) {
                val intent = Intent(this, PaymentActivity::class.java)
                intent.putExtra("filmTitle", film.Title)
                intent.putExtra("selectedSeats", selectedSeatNames)
                intent.putExtra("totalPrice", price)
                intent.putExtra("date", "20/10/2023")
                intent.putExtra("time", "10:00 AM")
                startActivity(intent)
            } else {
                Toast.makeText(this, "Vui lòng chọn ghế", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getIntentExtra() {
        film = intent.getParcelableExtra("film")!!
    }

    private fun generateTimeSlots(): List<String> {
        val timeSlots = mutableListOf<String>()
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        for (i in 0 until 24 step 2) {
            val time = LocalTime.of(i, 0).format(formatter)
            timeSlots.add(time)
        }
        return timeSlots
    }

    private fun generateDate(): List<String> {
        val dates = mutableListOf<String>()
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        for (i in 0 until 7) {
            dates.add(today.plusDays(i.toLong()).format(formatter))
        }
        return dates
    }
}