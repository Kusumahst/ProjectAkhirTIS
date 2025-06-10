package com.example.projectakhirtis.model

import java.io.Serializable

data class Ticket(
    val id: Long = 0, // ← default value OK
    val id_tiket: String,
    val nama_bus: String,
    val harga: Int,
    val fasilitas: String,
    val waktu_berangkat: String,
    val rute_keberangkatan: String,
    val rute_tujuan: String,
    val created_at: String? = null,
    val updated_at: String? = null
): Serializable
