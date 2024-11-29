package com.yonasputra.absensiswa

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AwalActivity : AppCompatActivity() {
    // Mendefinisikan referensi Firebase dengan URL yang benar
    private val database =
        FirebaseDatabase.getInstance("https://absensiswa22-dcf48-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Absensi")

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_awal)

        // Mendapatkan referensi tombol dari XML
        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)
        val btn3: Button = findViewById(R.id.btn3)
        val btn4: Button = findViewById(R.id.btn4)

        // Listener untuk tombol btn1 (pindah ke MainActivity)
        btn1.setOnClickListener {
            try {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                showToast("Navigasi ke MainActivity")
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
                Log.e("AwalActivity", "Intent Error: ${e.message}")
            }
        }

        // Listener untuk tombol btn2, btn3, dan btn4
        btn2.setOnClickListener {
            try {
                val intent = Intent(this, DispensasiActivity::class.java)
                startActivity(intent)
                showToast("Navigasi ke MainActivity")
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
                Log.e("AwalActivity", "Intent Error: ${e.message}")
            }
        }


        btn3.setOnClickListener {
            try {
                val intent = Intent(this, AbsenActivity::class.java)
                startActivity(intent)
                showToast("Navigasi ke MainActivity")
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
                Log.e("AwalActivity", "Intent Error: ${e.message}")
            }
        }
        btn4.setOnClickListener {
            try {
                val intent = Intent(this, MainActivityDispensasi::class.java)
                startActivity(intent)
                showToast("Navigasi ke MainActivity")
            } catch (e: Exception) {
                showToast("Error: ${e.message}")
                Log.e("AwalActivity", "Intent Error: ${e.message}")
            }
        }



        // Fungsi untuk menampilkan pesan toast

    }
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
