package com.example.ordertickets.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ordertickets.Adapter.ProfileAdapter
import com.example.ordertickets.R

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val rvProfile = findViewById<RecyclerView>(R.id.rvProfile)

        val menuItems =
            listOf("Thông tin tài khoản", "Lịch sử đặt vé", "Cài đặt", "Đăng xuất", "Vé của tôi")

        val adapter = ProfileAdapter(menuItems) { selectedItem ->
            when (selectedItem) {
                "Đăng xuất" -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                "Vé của tôi" -> {
                    val intent = Intent(this, MyTicketsActivity::class.java)
                    startActivity(intent)
                }
            }
        }

        rvProfile.layoutManager = LinearLayoutManager(this)
        rvProfile.adapter = adapter
        
        findViewById<ImageView>(R.id.imgBackProfile).setOnClickListener {
            finish()
        }
    }
}
