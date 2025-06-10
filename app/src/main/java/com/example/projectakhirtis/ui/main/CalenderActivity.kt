package com.example.projectakhirtis.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.CalendarContract
import android.widget.CalendarView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.projectakhirtis.R
import java.text.SimpleDateFormat
import java.util.*

class CalenderActivity : AppCompatActivity() {
    private lateinit var calendarView: CalendarView
    private lateinit var tvTanggal: TextView
    private lateinit var tvEventList: TextView
    private var selectedDate: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calender)

        calendarView = findViewById(R.id.calendarView)
        tvTanggal = findViewById(R.id.tvTanggalDipilih)
        tvEventList = findViewById(R.id.tvEventList)

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth, 0, 0, 0)
            selectedDate = cal.timeInMillis
            updateUI()
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALENDAR), 101)
        } else {
            updateUI()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 101 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            updateUI()
        } else {
            Toast.makeText(this, "Permission ditolak", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUI() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.timeInMillis = selectedDate
        val tanggal = dateFormat.format(cal.time)

        tvTanggal.text = "Tanggal dipilih: $tanggal"
        tvEventList.text = "Daftar Event:\n" + getEventsForDate(selectedDate)
    }

    private fun getEventsForDate(timeInMillis: Long): String {
        val events = StringBuilder()
        val startOfDay = Calendar.getInstance()
        startOfDay.timeInMillis = timeInMillis
        startOfDay.set(Calendar.HOUR_OF_DAY, 0)
        startOfDay.set(Calendar.MINUTE, 0)
        startOfDay.set(Calendar.SECOND, 0)
        startOfDay.set(Calendar.MILLISECOND, 0)

        val endOfDay = Calendar.getInstance()
        endOfDay.timeInMillis = timeInMillis
        endOfDay.set(Calendar.HOUR_OF_DAY, 23)
        endOfDay.set(Calendar.MINUTE, 59)
        endOfDay.set(Calendar.SECOND, 59)
        endOfDay.set(Calendar.MILLISECOND, 999)

        val projection = arrayOf(
            CalendarContract.Events.TITLE,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.EVENT_LOCATION
        )

        val selection = "((${CalendarContract.Events.DTSTART} >= ?) AND (${CalendarContract.Events.DTSTART} <= ?))"
        val selectionArgs = arrayOf(
            startOfDay.timeInMillis.toString(),
            endOfDay.timeInMillis.toString()
        )

        val cursor: Cursor? = contentResolver.query(
            CalendarContract.Events.CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            null
        )

        if (cursor != null && cursor.moveToFirst()) {
            do {
                val title = cursor.getString(0) ?: "Tanpa Judul"
                val waktu = Date(cursor.getLong(1))
                val lokasi = cursor.getString(2) ?: "-"
                val jam = SimpleDateFormat("HH:mm", Locale.getDefault()).format(waktu)
                events.append("• $title\n  $jam @ $lokasi\n\n")
            } while (cursor.moveToNext())
            cursor.close()
        } else {
            events.append("Tidak ada event pada tanggal ini.")
        }

        return events.toString()
    }
}
