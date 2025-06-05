package com.example.projectakhirtis.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projectakhirtis.network.ApiClient
import com.example.projectakhirtis.R
import com.example.projectakhirtis.helper.TokenManager
import com.example.projectakhirtis.ui.auth.LoginActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var ticketRecyclerView: RecyclerView
    private lateinit var adapter: TicketAdapter
    private lateinit var logoutBtn: Button
    private lateinit var addTicketBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ticketRecyclerView = findViewById(R.id.ticketRecyclerView)
        logoutBtn = findViewById(R.id.btnLogout)
        addTicketBtn = findViewById(R.id.btnAddTicket)

        ticketRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter = TicketAdapter()
        ticketRecyclerView.adapter = adapter

        loadTickets()

        logoutBtn.setOnClickListener {
            TokenManager.clearToken(this)
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        addTicketBtn.setOnClickListener {
            startActivity(Intent(this, AddTicketActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        loadTickets()
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
