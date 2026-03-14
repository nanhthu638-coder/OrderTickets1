package com.example.ordertickets.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ordertickets.R

class LoginActivity : AppCompatActivity() {

    private var isLoginMode = true // true: Đăng nhập, false: Đăng ký

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtPassword = findViewById<EditText>(R.id.edtPassword)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val txtSwitch = findViewById<TextView>(R.id.txtSwitch)
        val txtTitle = findViewById<TextView>(R.id.txtTitle)
        fun performLogin(email: String, pass: String) {
            // 1. Kiểm tra tài khoản (bỏ qua bước này nếu đang làm giao diện)

            // 2. Nếu thành công, chuyển sang ProfileActivity
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish() // Đóng LoginActivity lại để người dùng không bấm back quay lại được
        }

        // 1. Chuyển đổi giữa chế độ Đăng nhập và Đăng ký
        txtSwitch.setOnClickListener {
            isLoginMode = !isLoginMode
            if (isLoginMode) {
                txtTitle.text = "Đăng Nhập"
                btnSubmit.text = "Đăng Nhập"
                txtSwitch.text = "Chưa có tài khoản? Đăng ký ngay"
            } else {
                txtTitle.text = "Đăng Ký Tài Khoản"
                btnSubmit.text = "Tạo Tài Khoản"
                txtSwitch.text = "Đã có tài khoản? Đăng nhập"
            }
        }


        // 2. Xử lý sự kiện khi nhấn nút chính
        btnSubmit.setOnClickListener {
            val email = edtEmail.text.toString()
            val pass = edtPassword.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isLoginMode) {
                // Logic Đăng nhập (Gửi API hoặc check DB)
                performLogin(email, pass)
            } else {
                // Logic Đăng ký
                performRegister(email, pass)
            }
        }
    }
    private fun saveLoginStatus(status: Boolean) {
        val sharedPref = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("isLoggedIn", status)
        editor.apply()
    }

    private fun performLogin(email: String, pass: String) {
        // Giả sử đăng nhập thành công
        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()

        // Trả kết quả về cho MainActivity hoặc chuyển trang
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("IS_LOGGED_IN", true)
        startActivity(intent)
        finish()
    }

    private fun performRegister(email: String, pass: String) {
        // Giả sử đăng ký thành công
        Toast.makeText(this, "Đăng ký thành công! Hãy đăng nhập.", Toast.LENGTH_SHORT).show()
        isLoginMode = true
        // Cập nhật lại UI về màn hình login
        findViewById<TextView>(R.id.txtSwitch).performClick()
    }
}

