package com.example.projectakhirtis.ui.main

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirtis.R
import com.example.projectakhirtis.helper.TokenManager
import com.example.projectakhirtis.ui.auth.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var btnLogout: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        etNama = findViewById(R.id.etNama)
        etEmail = findViewById(R.id.etEmail)
        btnLogout = findViewById(R.id.btnLogout)
        bottomNavigationView = findViewById(R.id.bottomNavigation)

        // Ambil data user dari SharedPreferences
        val prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
        val name = prefs.getString("USER_NAME", "")
        val email = prefs.getString("USER_EMAIL", "")

        etNama.setText(name)
        etEmail.setText(email)

        btnLogout.setOnClickListener {
            TokenManager.clearToken(this)
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
            finish()
        }

        setupBottomNavigation()
        bottomNavigationView.selectedItemId = R.id.menu_profile
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
                R.id.menu_tambah -> {
                    startActivity(Intent(this, AddTicketActivity::class.java))
                    true
                }
                R.id.menu_profile -> {true
                }
                else -> false
            }
        }
    }
}
