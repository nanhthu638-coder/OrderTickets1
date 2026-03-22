package com.example.ordertickets.Activity

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ordertickets.Models.Ticket
import com.example.ordertickets.databinding.ActivityPaymentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )

        displayOrderSummary()
        setVariables()
    }

    private fun displayOrderSummary() {
        val filmTitle = intent.getStringExtra("filmTitle") ?: "N/A"
        val selectedSeats = intent.getStringExtra("selectedSeats") ?: "N/A"
        val totalPrice = intent.getDoubleExtra("totalPrice", 0.0)
        val date = intent.getStringExtra("date") ?: "N/A"
        val time = intent.getStringExtra("time") ?: "N/A"

        binding.tvFilmTitle.text = filmTitle
        binding.tvSummary.text = "Ghế: $selectedSeats\nNgày: $date\nGiờ: $time"
        binding.tvTotalPrice.text = "$$totalPrice"
    }

    private fun setVariables() {
        binding.imgBack.setOnClickListener { finish() }

        binding.btnConfirmPayment.setOnClickListener {
            val selectedId = binding.rgPaymentMethod.checkedRadioButtonId
            if (selectedId == -1) {
                Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show()
            } else {
                saveTicketToFirebase()
            }
        }
    }
    private fun saveTicketToFirebase() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để thực hiện thanh toán", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance().getReference("Tickets")
        val ticketId = database.push().key ?: ""

        // Lấy dữ liệu từ intent để tạo đối tượng Ticket
        val ticket = Ticket(
            id = ticketId,
            filmTitle = intent.getStringExtra("filmTitle") ?: "",
            selectedSeats = intent.getStringExtra("selectedSeats") ?: "",
            totalPrice = intent.getDoubleExtra("totalPrice", 0.0),
            date = intent.getStringExtra("date") ?: "",
            time = intent.getStringExtra("time") ?: "",
            userId = currentUser.uid
        )

        // Lưu lên Firebase Realtime Database
        database.child(ticketId).setValue(ticket)
            .addOnSuccessListener {
                Toast.makeText(this, "Thanh toán và lưu vé thành công!", Toast.LENGTH_SHORT).show()

                // Sau khi lưu xong, chuyển về trang chủ
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi khi lưu vé: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
