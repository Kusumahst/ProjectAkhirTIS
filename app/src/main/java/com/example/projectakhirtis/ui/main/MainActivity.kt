package com.example.projectakhirtis.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectakhirtis.R
import com.example.projectakhirtis.helper.TokenManager
import com.example.projectakhirtis.network.ApiClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var ticketRecyclerView: RecyclerView
    private lateinit var adapter: TicketAdapter
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi view
        ticketRecyclerView = findViewById(R.id.ticketRecyclerView)
        bottomNavigationView = findViewById(R.id.bottomNavigation)

        // Setup RecyclerView
        ticketRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TicketAdapter()
        ticketRecyclerView.adapter = adapter

        // Setup navigasi bawah
        setupBottomNavigation()

        // Load data tiket
        loadTickets()
    }

    override fun onResume() {
        super.onResume()
        loadTickets()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> true
                R.id.menu_kalender -> {
                    Toast.makeText(this, "Menu Jadwal belum tersedia", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.menu_tambah -> {
                    startActivity(Intent(this, AddTicketActivity::class.java))
                    true
                }
                R.id.menu_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun loadTickets() {
        val token = TokenManager.getToken(this)

        if (token.isNullOrEmpty()) {
            Toast.makeText(this, "Token tidak ditemukan", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiClient.apiService.getTickets("Bearer $token")
                withContext(Dispatchers.Main) {
                    adapter.setData(response)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Gagal memuat tiket", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
