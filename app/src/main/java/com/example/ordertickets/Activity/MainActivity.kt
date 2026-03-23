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
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dataBase: FirebaseDatabase
    private val sliderHandler = Handler(Looper.getMainLooper())
    private val sliderRunnable = Runnable {
        binding.viewPager2.currentItem =
            (binding.viewPager2.currentItem + 1) % (binding.viewPager2.adapter?.itemCount ?: 1)
    }
    private var topMoviesList = ArrayList<Film>() // Khởi tạo danh sách trống
    private lateinit var topMoviesAdapter: FilmListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        dataBase = FirebaseDatabase.getInstance()
        
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        
        initBanner()
        initTopMoving()
        initUpcomingMovies()
        setupSearch()
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        binding.chipNavigationBar.setItemSelected(R.id.explorer, true)

        binding.chipNavigationBar.setOnItemSelectedListener { id ->
            when (id) {
                R.id.profile -> {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    if (currentUser == null) {
                        startActivity(Intent(this, LoginActivity::class.java))
                    } else {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }
                }
                R.id.explorer -> {
                }
            }
        }
    }

    private fun setupSearch() {
        binding.editTextText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val keyword = s?.toString() ?: ""

                // Kiểm tra nếu danh sách hoặc adapter đã được khởi tạo chưa
                if (::topMoviesAdapter.isInitialized && topMoviesList.isNotEmpty()) {
                    val filteredList = topMoviesList.filter { film ->
                        film.Title?.contains(keyword, ignoreCase = true) == true
                    }
                    topMoviesAdapter.updateList(filteredList)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun initBanner() {
        val myRef: DatabaseReference = dataBase.getReference("Banners")
        binding.pgBSlider.visibility = View.VISIBLE
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists = mutableListOf<Slideritems>()
                for (childSnapshot in snapshot.children) {
                    val list = childSnapshot.getValue(Slideritems::class.java)
                    if (list != null) {
                        lists.add(list)
                    }
                }
                binding.pgBSlider.visibility = View.GONE
                banners(lists)
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun initTopMoving() {
        val myRef: DatabaseReference = dataBase.getReference("Items")
        binding.pgBTopMovies.visibility = View.VISIBLE
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    topMoviesList.clear() // Xóa list cũ trước khi add mới
                    for (issue in snapshot.children) {
                        val film = issue.getValue(Film::class.java)
                        if (film != null) {
                            topMoviesList.add(film)
                        }
                    }
                    if (topMoviesList.isNotEmpty()) {
                        binding.rvTopMovies.layoutManager = LinearLayoutManager(
                            this@MainActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        topMoviesAdapter = FilmListAdapter(topMoviesList)
                        binding.rvTopMovies.adapter = topMoviesAdapter
                    }
                    binding.pgBTopMovies.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun initUpcomingMovies() {
        val myRef: DatabaseReference = dataBase.getReference("Upcomming")
        binding.pgBUpcomingMV.visibility = View.VISIBLE
        val items = ArrayList<Film>()
        myRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (issue in snapshot.children) {
                        items.add(issue.getValue(Film::class.java)!!)
                    }
                    if (items.isNotEmpty()) {
                        binding.rvUpcomingMV.layoutManager = LinearLayoutManager(
                            this@MainActivity,
                            LinearLayoutManager.HORIZONTAL,
                            false
                        )
                        binding.rvUpcomingMV.adapter = FilmListAdapter(items)
                    }
                    binding.pgBUpcomingMV.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    private fun banners(lists: MutableList<Slideritems>) {
        binding.viewPager2.adapter = SliderAdapter(lists, binding.viewPager2)
        binding.viewPager2.clipToPadding = false
        binding.viewPager2.clipChildren = false
        binding.viewPager2.offscreenPageLimit = 3
        binding.viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer().apply {
            addTransformer(MarginPageTransformer(40))
            addTransformer(ViewPager2.PageTransformer { page, position ->
                val r = 1 - Math.abs(position)
                page.scaleY = 0.85f + r * 0.15f
            })
        }
        binding.viewPager2.setPageTransformer(compositePageTransformer)
        binding.viewPager2.currentItem = 1
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 2000)
            }
        })
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable)
    }

    override fun onResume() {
        super.onResume()
        sliderHandler.postDelayed(sliderRunnable, 2000)
    }
}
