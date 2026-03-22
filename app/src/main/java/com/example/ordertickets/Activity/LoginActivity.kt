package com.example.ordertickets.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
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
        val edtConfirmPassword = findViewById<EditText>(R.id.edtConfirmPassword)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val txtSwitch = findViewById<TextView>(R.id.txtSwitch)
        val txtTitle = findViewById<TextView>(R.id.txtTitle)

        // 1. Chuyển đổi giữa chế độ Đăng nhập và Đăng ký
        txtSwitch.setOnClickListener {
            isLoginMode = !isLoginMode
            if (isLoginMode) {
                txtTitle.text = "ĐĂNG NHẬP"
                btnSubmit.text = "ĐĂNG NHẬP"
                txtSwitch.text = "Chưa có tài khoản? Đăng ký ngay"
                edtConfirmPassword.visibility = View.GONE
            } else {
                txtTitle.text = "ĐĂNG KÝ TÀI KHOẢN"
                btnSubmit.text = "TẠO TÀI KHOẢN"
                txtSwitch.text = "Đã có tài khoản? Đăng nhập"
                edtConfirmPassword.visibility = View.VISIBLE
            }
        }

        // 2. Xử lý sự kiện khi nhấn nút chính
        btnSubmit.setOnClickListener {
            val email = edtEmail.text.toString()
            val pass = edtPassword.text.toString()
            val confirmPass = edtConfirmPassword.text.toString()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isLoginMode) {
                performLogin(email, pass)
            } else {
                if (pass != confirmPass) {
                    Toast.makeText(this, "Mật khẩu nhập lại không khớp", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                performRegister(email, pass)
            }
        }
    }

    private fun performLogin(email: String, pass: String) {
        // Giả sử đăng nhập thành công
        Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun performRegister(email: String, pass: String) {
        // Logic đăng ký tài khoản (ví dụ lưu vào Firebase hoặc Database)
        // ...
        
        Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
        
        // Điều hướng thẳng đến trang Profile sau khi đăng ký thành công
        val intent = Intent(this, ProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}
