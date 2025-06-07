package com.example.projectakhirtis.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirtis.R
import com.example.projectakhirtis.helper.TokenManager
import com.example.projectakhirtis.model.Ticket
import com.example.projectakhirtis.network.ApiClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTicketActivity : AppCompatActivity() {

    private lateinit var etIdTiket: EditText
    private lateinit var etNamaBus: EditText
    private lateinit var etHarga: EditText
    private lateinit var etFasilitas: EditText
    private lateinit var etWaktuBerangkat: EditText
    private lateinit var etRuteKeberangkatan: EditText
    private lateinit var etRuteTujuan: EditText
    private lateinit var btnSubmit: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ticket)

        etIdTiket = findViewById(R.id.etIdTiket)
        etNamaBus = findViewById(R.id.etNamaBus)
        etHarga = findViewById(R.id.etHarga)
        etFasilitas = findViewById(R.id.etFasilitas)
        etWaktuBerangkat = findViewById(R.id.etWaktuBerangkat)
        etRuteKeberangkatan = findViewById(R.id.etRuteKeberangkatan)
        etRuteTujuan = findViewById(R.id.etRuteTujuan)
        btnSubmit = findViewById(R.id.btnSubmit)
        bottomNavigationView = findViewById(R.id.bottomNavigation)

        btnSubmit.setOnClickListener {
            val ticket = Ticket(
                id_tiket = etIdTiket.text.toString(),
                nama_bus = etNamaBus.text.toString(),
                harga = etHarga.text.toString().toIntOrNull() ?: 0,
                fasilitas = etFasilitas.text.toString(),
                waktu_berangkat = etWaktuBerangkat.text.toString(),
                rute_keberangkatan = etRuteKeberangkatan.text.toString(),
                rute_tujuan = etRuteTujuan.text.toString()
            )

            addTicket(ticket)
        }

        setupBottomNavigation()
        bottomNavigationView.selectedItemId = R.id.menu_tambah
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.menu_kalender -> {
                    Toast.makeText(this, "Menu Jadwal belum tersedia", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_tambah -> true
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
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
