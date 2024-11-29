package com.yonasputra.absensiswa

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import android.app.AlertDialog
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner

class AbsenActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://absensiswa22-dcf48-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val databaseReference = database.getReference("absensi")

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AbsenAdapter
    private val dataList = ArrayList<ModelAbsen>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_absen)

        val backabsen = findViewById<ImageButton>(R.id.backabsen)
        backabsen.setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup adapter with callbacks
        adapter = AbsenAdapter(
            dataList,
            onEditClick = ::showEditDialog,
            onDeleteClick = { position ->
                deleteDataFromDatabase(position)
            }
        )
        recyclerView.adapter = adapter

        // Fetch data from Firebase Realtime Database
        fetchDataFromDatabase()
    }

    private fun fetchDataFromDatabase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Clear dataList to avoid duplication
                dataList.clear()
                for (dataSnapshot in snapshot.children) {
                    val data = dataSnapshot.value
                    // Log the raw data to inspect it
                    Log.d("Firebase", "Data: $dataSnapshot")

                    // Check if the data is a Map (the correct format for ModelAbsen)
                    if (data is Map<*, *>) {
                        // Try to convert data to ModelAbsen
                        val absen = dataSnapshot.getValue(ModelAbsen::class.java)
                        if (absen != null) {
                            absen.key = dataSnapshot.key // Save the key for editing/deleting
                            dataList.add(absen)
                        } else {
                            Log.e("Firebase", "Error: Cannot convert data at ${dataSnapshot.key}")
                        }
                    } else {
                        Log.e("Firebase", "Invalid data format at ${dataSnapshot.key}: $data")
                    }
                }
                // Notify adapter after fetching data
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching data", error.toException())
            }
        })
    }

    private fun showEditDialog(data: ModelAbsen, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_absen, null)

        // Correcting the view references
        val etNama = dialogView.findViewById<EditText>(R.id.etNamaSiswa)
        val etKelas = dialogView.findViewById<Spinner>(R.id.spinnerKelas)
        val etStatus = dialogView.findViewById<Spinner>(R.id.spinnerStatus)

        // Pre-fill fields with existing data
        etNama.setText(data.namaSiswa)

        // Set the spinner selection based on the current 'kelas' data
        val kelasArray = resources.getStringArray(R.array.kelas_array)
        val kelasAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, kelasArray)
        etKelas.adapter = kelasAdapter
        val kelasPosition = kelasArray.indexOf(data.kelas)  // Find the index for 'data.kelas'
        if (kelasPosition != -1) {
            etKelas.setSelection(kelasPosition)  // Set the correct spinner position
        }

        // Set the spinner for status based on the current 'status' data
        val statusArray = resources.getStringArray(R.array.status_kehadiran)
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, statusArray)
        etStatus.adapter = statusAdapter
        val statusPosition = statusArray.indexOf(data.status)  // Find the index for 'data.status'
        if (statusPosition != -1) {
            etStatus.setSelection(statusPosition)  // Set the correct spinner position
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Absen")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                // Update data in Firebase
                val updatedData = ModelAbsen(
                    namaSiswa = etNama.text.toString(),
                    kelas = etKelas.selectedItem.toString(),  // Get the selected item from the Spinner
                    status = etStatus.selectedItem.toString(), // Get the selected item from the Spinner
                    jamUTC = data.jamUTC,
                    key = data.key // Pastikan key tetap ada di data baru
                )
                updateDataInDatabase(data.key ?: "", updatedData, position)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateDataInDatabase(key: String, updatedData: ModelAbsen, position: Int) {
        databaseReference.child(key).setValue(updatedData).addOnSuccessListener {
            // Tambahkan kembali key setelah pembaruan
            updatedData.key = key
            dataList[position] = updatedData
            adapter.notifyItemChanged(position)
        }.addOnFailureListener { e ->
            Log.e("Firebase", "Error updating data", e)
        }
    }

    private fun deleteDataFromDatabase(position: Int) {
        val key = dataList[position].key
        if (key != null) {
            // Menampilkan dialog konfirmasi sebelum menghapus data
            AlertDialog.Builder(this)
                .setTitle("Konfirmasi Penghapusan")
                .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                .setPositiveButton("Ya") { _, _ ->
                    // Lakukan penghapusan jika pengguna mengonfirmasi
                    databaseReference.child(key).removeValue().addOnSuccessListener {
                        fetchDataFromDatabase()
                    }.addOnFailureListener { e ->
                        Log.e("Firebase", "Error deleting data", e)
                    }
                }
                .setNegativeButton("Tidak", null) // Tidak melakukan apa-apa jika memilih "Tidak"
                .show()
        }
    }
}
