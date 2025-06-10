package com.example.projectakhirtis.helper

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.projectakhirtis.R
import com.example.projectakhirtis.model.Ticket
import com.example.projectakhirtis.network.ApiClient
import com.example.projectakhirtis.ui.main.EditTicketActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TicketAdapter: RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {
    private val tickets = mutableListOf<Ticket>()

    fun setData(newTickets: List<Ticket>) {
        tickets.clear()
        tickets.addAll(newTickets)
        notifyDataSetChanged()
    }

    class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNamaBus: TextView = itemView.findViewById(R.id.tvNamaBus)
        val tvHarga: TextView = itemView.findViewById(R.id.tvHarga)
        val tvKeberangkatan: TextView = itemView.findViewById(R.id.tvKeberangkatan)
        val tvTujuan: TextView = itemView.findViewById(R.id.tvTujuan)
        val tvWaktu: TextView = itemView.findViewById(R.id.tvWaktu)
        val tvFasilitas: TextView = itemView.findViewById(R.id.tvFasilitas)
        val btnPesan: Button = itemView.findViewById(R.id.btnPesan)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ticket_card_item, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.tvNamaBus.text = ticket.nama_bus
        holder.tvHarga.text = "Rp ${ticket.harga}"
        holder.tvKeberangkatan.text = ticket.rute_keberangkatan
        holder.tvTujuan.text = ticket.rute_tujuan
        holder.tvWaktu.text = ticket.waktu_berangkat
        holder.tvFasilitas.text = "Fasilitas: ${ticket.fasilitas}"

        holder.btnPesan.setOnClickListener {
            val context = holder.itemView.context

            try {
                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                val fullDateTime = "$today ${ticket.waktu_berangkat}"  // e.g. "2025-06-10 08:00"
                val date = formatter.parse(fullDateTime)

                if (date == null) {
                    Toast.makeText(context, "Format waktu salah", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val startMillis = date.time
                val endMillis = startMillis + 2 * 60 * 60 * 1000

                val intent = Intent(Intent.ACTION_INSERT).apply {
                    type = "vnd.android.cursor.item/event"
                    putExtra("title", "Perjalanan ${ticket.rute_keberangkatan} → ${ticket.rute_tujuan} dengan ${ticket.nama_bus}")
                    putExtra("description", "Fasilitas: ${ticket.fasilitas}")
                    putExtra("eventLocation", "${ticket.rute_keberangkatan} → ${ticket.rute_tujuan}")
                    putExtra("beginTime", startMillis)
                    putExtra("endTime", endMillis)
                }

                context.startActivity(intent)

            } catch (e: Exception) {
                Toast.makeText(context, "Gagal menambahkan ke kalender: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }



        holder.btnEdit.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, EditTicketActivity::class.java)
            intent.putExtra("ticket", ticket)
            context.startActivity(intent)
        }

        holder.btnDelete.setOnClickListener {
            val context = holder.itemView.context
            val token = TokenManager.getToken(context)

            if (token != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        val response = ApiClient.apiService.deleteTicket("Bearer $token", ticket.id_tiket.toString())
                        withContext(Dispatchers.Main) {
                            if (response.isSuccessful) {
                                Toast.makeText(context, "Tiket dihapus", Toast.LENGTH_SHORT).show()
                                tickets.removeAt(position)
                                notifyItemRemoved(position)
                            } else {
                                Toast.makeText(context, "Gagal hapus tiket", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
    override fun getItemCount(): Int = tickets.size
}