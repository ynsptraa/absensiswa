package com.yonasputra.absensiswa

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var etNamaSiswa: EditText
    private lateinit var spinnerKelas: Spinner
    private lateinit var spinnerStatus: Spinner
    private lateinit var btnAmbilAbsensi: Button
    private lateinit var listViewAbsensi: ListView
    private val absensiList = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private val database = FirebaseDatabase.getInstance("https://absensiswa22-dcf48-default-rtdb.asia-southeast1.firebasedatabase.app/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inisialisasi View
        etNamaSiswa = findViewById(R.id.etNamaSiswa)
        spinnerKelas = findViewById(R.id.spinnerKelas) // Updated to Spinner
        spinnerStatus = findViewById(R.id.spinnerStatus)
        btnAmbilAbsensi = findViewById(R.id.btnAmbilAbsensi)


        // Mengisi Spinner dengan data status kehadiran
        val statusArray = resources.getStringArray(R.array.status_kehadiran)
        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusArray)
        spinnerStatus.adapter = spinnerAdapter

        // Inisialisasi tombol kembali
        val backabsen = findViewById<ImageButton>(R.id.backabsen)
        backabsen.setOnClickListener {
            finish()
        }

        // Event klik tombol ambil absensi
        btnAmbilAbsensi.setOnClickListener {
            val namaSiswa = etNamaSiswa.text.toString().trim()
            val kelas = spinnerKelas.selectedItem?.toString()?.trim() ?: "" // Use the Spinner value
            val status = spinnerStatus.selectedItem?.toString() ?: ""

            // Mengambil waktu server dalam format UTC
            val jamUTC = getCurrentUTCTime()

            // Validasi input
            if (namaSiswa.isNotEmpty() && kelas.isNotEmpty() && status.isNotEmpty()) {
                val absen = ModelAbsen(namaSiswa, kelas, status, jamUTC)
                saveAbsenToFirebase(absen)
                absensiList.add("Nama: $namaSiswa, Kelas: $kelas, Jam (UTC): $jamUTC, Status: $status")
                startActivity(Intent(this, AbsenActivity::class.java))
                finish()

                // Reset input
                etNamaSiswa.text.clear()
                spinnerKelas.setSelection(0) // Reset the Spinner to default selection
                spinnerStatus.setSelection(0) // Reset the Spinner to default selection
            } else {
                Toast.makeText(this, "Mohon isi semua data!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk menyimpan data ke Firebase
    private fun saveAbsenToFirebase(absen: ModelAbsen) {
        val absenRef = database.reference.child("absensi").push() // Generate a unique key
        val key = absenRef.key.toString() // Retrieve the generated key

        if (key != null) {
            absen.key = key // Set the key in the ModelAbsen object

            // Push the ModelAbsen object to Firebase with the generated key
            absenRef.setValue(absen)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Gagal menyimpan data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Gagal membuat key untuk data", Toast.LENGTH_SHORT).show()
        }
    }


    // Fungsi untuk mendapatkan waktu saat ini dalam UTC
    private fun getCurrentUTCTime(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        return dateFormat.format(calendar.time)
    }
}

