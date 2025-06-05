package com.example.projectakhirtis.ui.main

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirtis.R
import com.example.projectakhirtis.helper.TokenManager
import com.example.projectakhirtis.model.Ticket
import com.example.projectakhirtis.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTicketActivity : AppCompatActivity() {
    private lateinit var etNamaBus: EditText
    private lateinit var etHarga: EditText
    private lateinit var etFasilitas: EditText
    private lateinit var etWaktuBerangkat: EditText
    private lateinit var etRuteKeberangkatan: EditText
    private lateinit var etRuteTujuan: EditText
    private lateinit var btnSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ticket)

        etNamaBus = findViewById(R.id.etNamaBus)
        etHarga = findViewById(R.id.etHarga)
        etFasilitas = findViewById(R.id.etFasilitas)
        etWaktuBerangkat = findViewById(R.id.etWaktuBerangkat)
        etRuteKeberangkatan = findViewById(R.id.etRuteKeberangkatan)
        etRuteTujuan = findViewById(R.id.etRuteTujuan)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val ticket = Ticket(
                id_tiket = "", // biarkan kosong, biasanya di-generate server
                nama_bus = etNamaBus.text.toString(),
                harga = etHarga.text.toString().toIntOrNull() ?: 0,
                fasilitas = etFasilitas.text.toString(),
                waktu_berangkat = etWaktuBerangkat.text.toString(),
                rute_keberangkatan = etRuteKeberangkatan.text.toString(),
                rute_tujuan = etRuteTujuan.text.toString()
            )

            addTicket(ticket)
        }
    }

    private fun addTicket(ticket: Ticket) {
        val token = TokenManager.getToken(this)

        if (token == null) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.addTicket("Bearer $token", ticket)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@AddTicketActivity,
                            "Tiket berhasil ditambahkan",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddTicketActivity,
                            "Gagal menambahkan tiket: ${response.code()}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AddTicketActivity,
                        "Terjadi kesalahan: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
