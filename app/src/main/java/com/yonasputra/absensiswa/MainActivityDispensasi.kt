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

class MainActivityDispensasi : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://absensiswa22-dcf48-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val databaseReference = database.getReference("dispensasi")

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: DispensasiAdapter
    private val dataList = ArrayList<ModelDispen>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_dispensasi)

        val backabsen = findViewById<ImageButton>(R.id.backabsen)
        backabsen.setOnClickListener {
            finish()
        }

        recyclerView = findViewById(R.id.rvDataDispensasi)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Setup adapter with callbacks
        adapter = DispensasiAdapter(
            dataList,
            onEditClick = { data, position ->
                showEditDialog(data, position) // Show Edit Dialog when clicked
            },
            onDeleteClick = { position ->
                deleteDataFromDatabase(position) // Delete data on click
            }
        )
        recyclerView.adapter = adapter

        // Fetch data from Firebase Realtime Database
        fetchDataFromDatabase()
    }

    // Fetching data from Firebase and populating RecyclerView
    private fun fetchDataFromDatabase() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                dataList.clear()
                for (dataSnapshot in snapshot.children) {
                    val data = dataSnapshot.getValue(ModelDispen::class.java)
                    if (data != null) {
                        // Assuming the key is being passed inside the ModelDispen class
                        data.key = dataSnapshot.key // Set the key from the snapshot
                        dataList.add(data)
                    }
                }
                adapter.notifyDataSetChanged() // Notify adapter to update the RecyclerView
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching data", error.toException())
            }
        })
    }

    // Show dialog to edit data
    private fun showEditDialog(data: ModelDispen, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_dispensasi, null)

        val etNama = dialogView.findViewById<EditText>(R.id.etNamaSiswa)
        val etKelas = dialogView.findViewById<Spinner>(R.id.spinnerKelas)
        val etAlasan = dialogView.findViewById<EditText>(R.id.etAlasan)

        etNama.setText(data.namaSiswa)
        etAlasan.setText(data.alasan)

        // Set up Spinner for Kelas (Class)
        val kelasArray = resources.getStringArray(R.array.kelas_array)
        val kelasAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, kelasArray)
        etKelas.adapter = kelasAdapter
        val kelasPosition = kelasArray.indexOf(data.kelas)
        if (kelasPosition != -1) {
            etKelas.setSelection(kelasPosition)
        }

        // Set up alert dialog to edit the dispensasi
        AlertDialog.Builder(this)
            .setTitle("Edit Dispensasi")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                // Create an updated ModelDispen object with the preserved key
                val updatedData = ModelDispen(
                    namaSiswa = etNama.text.toString(),
                    kelas = etKelas.selectedItem.toString(),
                    alasan = etAlasan.text.toString(),
                    jamUTC = data.jamUTC,  // retain original jamUTC for key
                    jamKembali = data.jamKembali,
                    key = data.key // Preserve the original key
                )
                // Update the data using the key (not jamUTC)
                updateDataInDatabase(data.key ?: "", updatedData, position) // Update the database
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Update data in Firebase Realtime Database
    private fun updateDataInDatabase(key: String, updatedData: ModelDispen, position: Int) {
        // Ensure we're only updating the values, not the key
        databaseReference.child(key).setValue(updatedData).addOnSuccessListener {
            dataList[position] = updatedData
            adapter.notifyItemChanged(position) // Notify adapter about item update
        }.addOnFailureListener { e ->
            Log.e("Firebase", "Error updating data", e)
        }
    }

    // Delete data from Firebase Realtime Database
    // Delete data from Firebase Realtime Database
    private fun deleteDataFromDatabase(position: Int) {
        // Ensure the dataList is not empty and position is valid
        if (dataList.isNotEmpty() && position >= 0 && position < dataList.size) {
            val key = dataList[position].key
            if (key != null) {
                AlertDialog.Builder(this)
                    .setTitle("Konfirmasi Penghapusan")
                    .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                    .setPositiveButton("Ya") { _, _ ->
                        databaseReference.child(key).removeValue().addOnSuccessListener {
                            // Remove item from the list if the deletion was successful
                            fetchDataFromDatabase()
                        }.addOnFailureListener { e ->
                            Log.e("Firebase", "Error deleting data", e)
                        }
                    }
                    .setNegativeButton("Tidak", null)
                    .show()
            }
        } else {
            Log.e("Delete", "Invalid position or empty list")
        }
    }

}
