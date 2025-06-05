package com.example.projectakhirtis.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.projectakhirtis.R
import com.example.projectakhirtis.model.Ticket

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
            Toast.makeText(holder.itemView.context, "Pesan ${ticket.nama_bus}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun getItemCount(): Int = tickets.size
}
