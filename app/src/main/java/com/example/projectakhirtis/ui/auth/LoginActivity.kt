package com.example.projectakhirtis.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirtis.ui.main.MainActivity
import com.example.projectakhirtis.R
import com.example.projectakhirtis.helper.TokenManager
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class LoginActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    interface ApiService {
        @POST("auth/login")
        fun login(@Body credentials: Map<String, String>): Call<AuthResponse>
    }

    data class AuthResponse(
        val token: String?,
        val name: String?,
        val email: String?
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)  // Diperbaiki: inisialisasi btnRegister

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi semua kolom", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val retrofit = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8001/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val service = retrofit.create(ApiService::class.java)
            val body = mapOf("email" to email, "password" to password)

            service.login(body).enqueue(object : Callback<AuthResponse> {
                override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                    if (response.isSuccessful && response.body()?.token != null) {
                        val data = response.body()!!
                        val token = data.token!!
                        val name = data.name ?: ""
                        val email = data.email ?: ""

                        // Simpan token
                        TokenManager.saveToken(this@LoginActivity, token)

                        // Simpan nama dan email ke SharedPreferences
                        val prefs = getSharedPreferences("APP_PREFS", MODE_PRIVATE)
                        prefs.edit()
                            .putString("USER_NAME", name)
                            .putString("USER_EMAIL", email)
                            .apply()

                        Toast.makeText(this@LoginActivity, "Login sukses", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Login gagal", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    Log.e("Login", "Error: ${t.message}")
                    Toast.makeText(this@LoginActivity, "Gagal koneksi ke server", Toast.LENGTH_SHORT).show()
                }
            })
        }

        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
