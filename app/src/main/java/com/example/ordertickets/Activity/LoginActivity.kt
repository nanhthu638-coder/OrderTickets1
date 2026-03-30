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
import com.google.firebase.auth.FirebaseAuth

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
            val email = edtEmail.text.toString().trim()
            val pass = edtPassword.text.toString().trim()
            val confirmPass = edtConfirmPassword.text.toString().trim() // Thêm trim cho cả confirm password

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // KIỂM TRA ĐỊNH DẠNG EMAIL TẠI ĐÂY, thiếu  sẽ hiện ra thông báo thiểu gì
            if (!isValidEmail(email)) {
                Toast.makeText(this, "Email không đúng định dạng (ví dụ: abc@gmail.com)", Toast.LENGTH_SHORT).show()
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
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Sai email hoặc mật khẩu!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun performRegister(email: String, pass: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener {
                Toast.makeText(this, "Tạo tài khoản thành công!", Toast.LENGTH_SHORT).show()
                // Sau khi tạo xong, chuyển thẳng đến ProfileActivity như bạn mong muốn
                startActivity(Intent(this, ProfileActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Lỗi: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
