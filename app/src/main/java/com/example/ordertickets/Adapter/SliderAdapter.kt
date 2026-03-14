package com.example.ordertickets.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.ordertickets.Models.Slideritems
import com.example.ordertickets.databinding.ViewholderSliderBinding
import kotlinx.coroutines.Runnable

class SliderAdapter(private val sliderItems: MutableList<Slideritems>
, private val viewPager2: ViewPager2
): RecyclerView.Adapter<SliderAdapter.SliderViewHolder>(){
    //Lưu Context để: Glide load ảnh
    //Inflate layout
    private var context: Context?=null

    //Nhân đôi danh sách sliderItems để tạo cảm giác trượt vô hạn
    private val runnable = Runnable{
        sliderItems.addAll(sliderItems)
        notifyDataSetChanged() //cập nhật lại giao diện
    }
    //Mỗi ViewHolder = 1 slide
    inner class SliderViewHolder(private val binding: ViewholderSliderBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(sliderItem: Slideritems){
            val requestOptions=RequestOptions().transform(CenterCrop() , RoundedCorners(60))// Load ảnh bằng Glide, Bo góc ảnh 60px + crop giữa
            context?.let {
                //Load ảnh vào ImageView
                Glide.with(it)
                    .load(sliderItem.image)
                    .apply (requestOptions)
                    .into(binding.imgSlide)
            }
        }
    }
    //Tạo giao diện cho 1 slide
    // Inflate file XML viewholder_slider.xml
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SliderAdapter.SliderViewHolder {
        context=parent.context
        val binding= ViewholderSliderBinding.inflate(LayoutInflater.from(context),parent,false)
        return SliderViewHolder(binding)
    }
    //Gán dữ liệu cho slide tại vị trí position
    override fun onBindViewHolder(holder: SliderAdapter.SliderViewHolder, position: Int) {
        holder.bind(sliderItems[position])
        //Auto loop slider
        if(position==sliderItems.size-2) //Khi slider chạy gần tới cuối thi thêm dữ liệu mới vào list
        {
            viewPager2.post(runnable)
        }

    }
    //Số slide = số phần tử trong list
    override fun getItemCount(): Int =sliderItems.size
}