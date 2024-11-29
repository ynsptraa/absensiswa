package com.yonasputra.absensiswa

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class DispensasiActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://absensiswa22-dcf48-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    private lateinit var etNamaSiswa: EditText
    private lateinit var spinnerKelas: Spinner
    private lateinit var etAlasan: EditText
    private lateinit var etDurasi: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dispensasi)

        // Initialize views
        etNamaSiswa = findViewById(R.id.etNamaSiswa)
        spinnerKelas = findViewById(R.id.spinnerKelas)
        etAlasan = findViewById(R.id.etAlasan)
        etDurasi = findViewById(R.id.etKembaliJam)
        val btnAmbilAbsensi: Button = findViewById(R.id.btnAmbilAbsensi)
        val backabsen: ImageButton = findViewById(R.id.backabsen)

        // Set up spinner
        val kelasAdapter = ArrayAdapter.createFromResource(
            this, R.array.kelas_array, android.R.layout.simple_spinner_item
        )
        kelasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerKelas.adapter = kelasAdapter

        // Restore saved instance state
        savedInstanceState?.let {
            etNamaSiswa.setText(it.getString("namaSiswa"))
            spinnerKelas.setSelection(it.getInt("kelasIndex", 0))
            etAlasan.setText(it.getString("alasan"))
            etDurasi.setText(it.getString("durasi"))
        }

        // Back button listener
        backabsen.setOnClickListener { finish() }

        // Save data listener
        btnAmbilAbsensi.setOnClickListener {
            val namaSiswa = etNamaSiswa.text.toString().trim()
            val kelas = spinnerKelas.selectedItem.toString()
            val alasan = etAlasan.text.toString().trim()
            val durasi = etDurasi.text.toString().trim()

            if (namaSiswa.isEmpty() || alasan.isEmpty() || durasi.isEmpty()) {
                Toast.makeText(this, "Mohon isi semua kolom!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val durasiJam = durasi.toIntOrNull()
            if (durasiJam == null || durasiJam <= 0) {
                Toast.makeText(this, "Durasi harus berupa angka positif!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentTime = Date()
            val jamJakarta = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            }.format(currentTime)

            val jamKembali = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).apply {
                timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            }.format(Date(currentTime.time + durasiJam * 3600 * 1000))

            val dispensasi = ModelDispen(
                namaSiswa = namaSiswa,
                jamUTC = jamJakarta,
                kelas = kelas,
                alasan = alasan,
                jamKembali = jamKembali
            )

            val dispensasiRef = database.child("dispensasi").push()
            dispensasiRef.setValue(dispensasi).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
                    clearFields(etNamaSiswa, etAlasan, etDurasi)
                    startActivity(Intent(this, MainActivityDispensasi::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Gagal menyimpan data!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Save state before configuration change
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("namaSiswa", etNamaSiswa.text.toString())
        outState.putString("alasan", etAlasan.text.toString())
        outState.putString("durasi", etDurasi.text.toString())
        outState.putInt("kelasIndex", spinnerKelas.selectedItemPosition)
    }

    private fun clearFields(vararg editTexts: EditText) {
        editTexts.forEach { it.text.clear() }
    }
}
