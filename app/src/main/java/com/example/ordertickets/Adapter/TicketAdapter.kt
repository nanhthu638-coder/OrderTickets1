package com.example.ordertickets.Adapter

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.icu.text.DecimalFormat
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ordertickets.Models.Ticket
import com.example.ordertickets.databinding.ViewholderTicketBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import java.io.OutputStream

class TicketAdapter(private val tickets: List<Ticket>) :
    RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    class TicketViewHolder(val binding: ViewholderTicketBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val binding = ViewholderTicketBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TicketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        val df = DecimalFormat("#,###")
        holder.binding.tvTicketFilmTitle.text = ticket.filmTitle
        holder.binding.tvTicketSeats.text = "Ghế: ${ticket.selectedSeats}"
        holder.binding.tvTicketDateTime.text = "${ticket.date} - ${ticket.time}"
        holder.binding.tvTicketPrice.text = df.format(ticket.totalPrice) + " VND"

        holder.binding.tvCancelTicket.setOnClickListener {

            val database = FirebaseDatabase.getInstance().getReference("Tickets")

            database.child(ticket.id).removeValue()
                .addOnSuccessListener {
                    Toast.makeText(holder.itemView.context, "Huỷ vé thành công", Toast.LENGTH_SHORT).show()

                    (tickets as MutableList).removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, tickets.size)
                }
                .addOnFailureListener {
                    Toast.makeText(holder.itemView.context, "Huỷ vé thất bại", Toast.LENGTH_SHORT).show()
                }
        }

        holder.binding.tvDownTicket.setOnClickListener {
            val qrContent = """
        Film: ${ticket.filmTitle}
        Seats: ${ticket.selectedSeats}
        Date: ${ticket.date}
        Time: ${ticket.time}
        Price: ${ticket.totalPrice}
        ID: ${ticket.id}
    """.trimIndent()

            generateAndSaveQR(
                holder.itemView.context,
                qrContent,
                "ticket_${ticket.id}"
            )
        }
    }

    override fun getItemCount() = tickets.size

    fun generateAndSaveQR(context: Context, content: String, fileName: String) {
        try {
            // 1. Tạo QR Bitmap
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.encodeBitmap(
                content,
                BarcodeFormat.QR_CODE,
                500,
                500
            )

            // 2. Lưu vào bộ nhớ (Gallery)
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            val uri = context.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values
            )

            uri?.let {
                val stream: OutputStream? = context.contentResolver.openOutputStream(it)
                stream?.use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                    Toast.makeText(context, "Tải vé thành công", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Tải vé thất bại", Toast.LENGTH_SHORT).show()
        }
    }
}