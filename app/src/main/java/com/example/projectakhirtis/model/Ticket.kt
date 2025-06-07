package com.example.projectakhirtis.model

data class Ticket(
    val id_tiket: String?,
    val nama_bus: String,
    val harga: Int,
    val fasilitas: String,
    val waktu_berangkat: String,
    val rute_keberangkatan: String,
    val rute_tujuan: String
)
