package com.example.projectakhirtis.ui.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.projectakhirtis.R
import com.example.projectakhirtis.helper.TokenManager
import com.example.projectakhirtis.model.Ticket
import com.example.projectakhirtis.network.ApiClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditTicketActivity : AppCompatActivity() {

    private lateinit var etNamaBus: EditText
    private lateinit var etHarga: EditText
    private lateinit var etFasilitas: EditText
    private lateinit var etWaktuBerangkat: EditText
    private lateinit var etRuteKeberangkatan: EditText
    private lateinit var etRuteTujuan: EditText
    private lateinit var btnSubmit: Button
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    private var ticket: Ticket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_ticket)

        // Inisialisasi view
        toolbar = findViewById(R.id.toolbar)
        etNamaBus = findViewById(R.id.etNamaBus)
        etHarga = findViewById(R.id.etHarga)
        etFasilitas = findViewById(R.id.etFasilitas)
        etWaktuBerangkat = findViewById(R.id.etWaktuBerangkat)
        etRuteKeberangkatan = findViewById(R.id.etRuteKeberangkatan)
        etRuteTujuan = findViewById(R.id.etRuteTujuan)
        btnSubmit = findViewById(R.id.btnSubmit)

        // Ambil data tiket dari intent
        ticket = intent.getSerializableExtra("ticket") as? Ticket

        if (ticket == null) {
            Toast.makeText(this, "Data tiket tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Isi field dengan data tiket
        etNamaBus.setText(ticket?.nama_bus)
        etHarga.setText(ticket?.harga.toString())
        etFasilitas.setText(ticket?.fasilitas)
        etWaktuBerangkat.setText(ticket?.waktu_berangkat)
        etRuteKeberangkatan.setText(ticket?.rute_keberangkatan)
        etRuteTujuan.setText(ticket?.rute_tujuan)

        // Atur Toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowTitleEnabled(false)
            setHomeAsUpIndicator(ContextCompat.getDrawable(this@EditTicketActivity, R.drawable.ic_back)?.apply {
                setTint(Color.WHITE)
            })
        }

        btnSubmit.setOnClickListener {
            if (validateInput()) {
                val updatedTicket = ticket!!.copy(
                    nama_bus = etNamaBus.text.toString(),
                    harga = etHarga.text.toString().toIntOrNull() ?: 0,
                    fasilitas = etFasilitas.text.toString(),
                    waktu_berangkat = etWaktuBerangkat.text.toString(),
                    rute_keberangkatan = etRuteKeberangkatan.text.toString(),
                    rute_tujuan = etRuteTujuan.text.toString()
                )
                updateTicket(updatedTicket)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun validateInput(): Boolean {
        val namaBus = etNamaBus.text.toString()
        val harga = etHarga.text.toString()
        val fasilitas = etFasilitas.text.toString()
        val waktu = etWaktuBerangkat.text.toString()
        val ruteAsal = etRuteKeberangkatan.text.toString()
        val ruteTujuan = etRuteTujuan.text.toString()

        if (namaBus.isBlank() || harga.isBlank() || fasilitas.isBlank() ||
            waktu.isBlank() || ruteAsal.isBlank() || ruteTujuan.isBlank()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return false
        }

        if (harga.toIntOrNull() == null) {
            Toast.makeText(this, "Harga harus berupa angka", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun updateTicket(ticket: Ticket) {
        val token = TokenManager.getToken(this)
        if (token.isNullOrBlank()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.updateTicket("Bearer $token", ticket.id_tiket.toString(), ticket)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditTicketActivity, "Tiket berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditTicketActivity, "Gagal memperbarui tiket", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EditTicketActivity, "Terjadi kesalahan: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}
