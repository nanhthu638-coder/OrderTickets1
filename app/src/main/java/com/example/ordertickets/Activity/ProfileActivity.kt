package com.example.ordertickets.Activity

// Thư mục: com.example.ordertickets.Activity


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ordertickets.Adapter.ProfileAdapter // Import adapter của bạn
import com.example.ordertickets.R

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val rvProfile = findViewById<RecyclerView>(R.id.rvProfile)

        // 1. Tạo danh sách các mục hiển thị
        val menuItems = listOf("Thông tin tài khoản", "Lịch sử đặt vé", "Cài đặt", "Đăng xuất", "Vé của tôi")

        // 2. Thiết lập Adapter
        val adapter = ProfileAdapter(menuItems) { selectedItem ->
            if (selectedItem == "Đăng xuất") {
                // Logic Đăng xuất: Quay về màn hình Login và xóa lịch sử chuyển trang
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
            // Bạn có thể thêm các mục khác như "Lịch sử đặt vé" ở đây
        }

        // 3. Kết nối RecyclerView với Adapter
        rvProfile.layoutManager = LinearLayoutManager(this)
        rvProfile.adapter = adapter
    }
}