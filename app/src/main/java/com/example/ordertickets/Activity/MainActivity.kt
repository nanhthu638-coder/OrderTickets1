package com.example.ordertickets.Activity

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.example.ordertickets.Adapter.FilmListAdapter
import com.example.ordertickets.Adapter.SliderAdapter
import com.example.ordertickets.Models.Film
import com.example.ordertickets.Models.Slideritems
import com.example.ordertickets.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.os.Looper
import android.text.TextWatcher
import android.text.Editable
import com.example.ordertickets.R
import android.content.Intent
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding //ViewBinding → truy cập View không cần findViewById
    private lateinit var dataBase: FirebaseDatabase //Kết nối Firebase Realtime Database
    private val sliderHandler= Handler(Looper.getMainLooper()) //Dùng để hẹn giờ (timer) cho slider
    private val sliderRunnable = Runnable {binding.viewPager2.currentItem =
        (binding.viewPager2.currentItem + 1) % (binding.viewPager2.adapter?.itemCount ?: 1) } //Mỗi lần chạy → slider sang trang tiếp theo
    private lateinit var topMoviesList: ArrayList<Film>
    private lateinit var topMoviesAdapter: FilmListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Gắn layout bằng ViewBinding
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Kết nối Firebase
        dataBase= FirebaseDatabase.getInstance()
        //Banner tràn full màn hình
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        //Gọi hàm lấy dữ liệu banner
        initBanner()
        initTopMoving()
        initUpcomingMovies()
        setupSearch()
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        // Mặc định chọn icon đầu tiên (Explorer)
        binding.chipNavigationBar.setItemSelected(R.id.explorer, true)

        binding.chipNavigationBar.setOnItemSelectedListener { id ->
            when (id) {
                R.id.profile -> {
                    // Logic kiểm tra đăng nhập giống hệt nút Profile cũ của bạn
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser == null) {
                        startActivity(Intent(this, LoginActivity::class.java))
                    } else {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }
                }
                R.id.explorer -> {
                    // Có thể thêm logic quay về đầu trang hoặc load lại dữ liệu
                }
                // Bạn có thể thêm xử lý cho favorites hoặc cart tại đây
            }
        }



    }
    private fun setupSearch() {

        binding.editTextText.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                val keyword = s?.toString() ?: ""

                val filteredList = topMoviesList.filter { film ->
                    film.Title?.contains(keyword, ignoreCase = true) == true
                }

                topMoviesAdapter.updateList(filteredList)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }


    //initBanner() – LẤY DỮ LIỆU FIREBASE
    private fun initBanner() {
        //Trỏ tới node
        val myRef: DatabaseReference =dataBase.getReference("Banners")
        binding.pgBSlider.visibility= View.VISIBLE //Hiện ProgressBar khi đang load
        //Đọc dữ liệu 1 lần duy nhất (không realtime)
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            //Firebase trả về toàn bộ node Banners
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists=mutableListOf<Slideritems>() //Tạo danh sách slider
                for(childSnapshot in snapshot.children){
                    val list=childSnapshot.getValue(Slideritems::class.java) //Map dữ liệu Firebase → object Slideritems
                    if(list!=null){
                        lists.add(list)

                    }
                }
                //Ẩn loading → hiển thị slider
                binding.pgBSlider.visibility= View.GONE
                banners(lists)
            }


            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun initTopMoving() {
        val myRef: DatabaseReference =dataBase.getReference("Items")
        binding.pgBTopMovies.visibility= View.VISIBLE //Hiện ProgressBar khi đang load
        val items= ArrayList<Film>()
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(issue in snapshot.children){
                        items.add(issue.getValue(Film::class.java)!!)
                    }
                    if(items.isNotEmpty()){
                        binding.rvTopMovies.layoutManager= LinearLayoutManager(
                            this@MainActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false)
                        binding.rvTopMovies.adapter= FilmListAdapter(items)
                    }
                    binding.pgBTopMovies.visibility= View.GONE //Ẩn ProgressBar khi load xong
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun initUpcomingMovies() {
        val myRef: DatabaseReference =dataBase.getReference("Upcomming")
        binding.pgBUpcomingMV.visibility= View.VISIBLE //Hiện ProgressBar khi đang load
        val items= ArrayList<Film>()
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(issue in snapshot.children){
                        items.add(issue.getValue(Film::class.java)!!)
                    }
                    if(items.isNotEmpty()){
                        binding.rvUpcomingMV.layoutManager= LinearLayoutManager(
                            this@MainActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false)
                        binding.rvUpcomingMV.adapter= FilmListAdapter(items)
                    }
                    binding.pgBUpcomingMV.visibility= View.GONE //Ẩn ProgressBar khi load xong
                }

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }


    private fun banners(lists: MutableList<Slideritems>) {
        binding.viewPager2.adapter= SliderAdapter(lists, binding.viewPager2) //Kết nối dữ liệu với ViewPager2
            binding.viewPager2.clipToPadding=false
            binding.viewPager2.clipChildren=false
            binding.viewPager2.offscreenPageLimit=3
            binding.viewPager2.getChildAt(0).overScrollMode= RecyclerView.OVER_SCROLL_NEVER //Không có hiệu ứng “bật lại” khi kéo

        val compositePageTransformer= CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
            addTransformer(ViewPager2.PageTransformer{page, position ->
                val r=1-Math.abs(position)
                page.scaleY=0.85f + r * 0.15f // Slide ở giữa → to nhất, Slide bên → nhỏ hơn
            })
        }
        binding.viewPager2.setPageTransformer(compositePageTransformer)
        binding.viewPager2.currentItem=1 //Tránh lỗi trượt vô hạn (do nhân đôi list)
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)// Dừng auto slide để: Tránh giật, Tránh xung đột Handler
                sliderHandler.postDelayed(sliderRunnable, 2000)
            }
        })


    }
    //Dừng auto slide khi app nền
    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }
    //Sau 2 giây → slider chạy lại
    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 2000)
    }
}




