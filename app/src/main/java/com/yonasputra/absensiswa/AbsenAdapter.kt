package com.yonasputra.absensiswa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class AbsenAdapter(
    private val dataList: List<ModelAbsen>,
    private val onEditClick: (ModelAbsen, Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<AbsenAdapter.AbsenViewHolder>() {

    // Menggunakan LayoutInflater untuk menampilkan item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbsenViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_absen, parent, false)
        return AbsenViewHolder(view)
    }

    // ViewHolder untuk mengelola tampilan item
    class AbsenViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tv_nama)
        val tvTanggal: TextView = itemView.findViewById(R.id.tv_tanggal)
        val tvKelas: TextView = itemView.findViewById(R.id.tv_Kelas)
        val tvStatus: TextView = itemView.findViewById(R.id.tv_status)
        val btnEdit: Button = itemView.findViewById(R.id.btn_edit)
        val btnDelete: Button = itemView.findViewById(R.id.btn_hapus)
    }

    // Fungsi untuk mendapatkan waktu saat ini dalam zona waktu Jakarta
    fun getCurrentJakartaTime(): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Jakarta"))
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
        return dateFormat.format(calendar.time)
    }

    // Mengisi data ke dalam ViewHolder
    override fun onBindViewHolder(holder: AbsenViewHolder, position: Int) {
        val data = dataList[position]

        // Mengatur data ke TextView
        holder.tvNama.text = data.namaSiswa
        holder.tvTanggal.text = data.jamUTC
        holder.tvKelas.text = data.kelas
        if (data.status == "Hadir") {
            holder.tvStatus.text = "Hadir"
            holder.tvStatus.setTextColor(holder.itemView.context.resources.getColor(R.color.hadir_color))
        } else if (data.status == "Izin") {
            holder.tvStatus.text = "Izin"
            holder.tvStatus.setTextColor(holder.itemView.context.resources.getColor(R.color.izin_color))
        } else if (data.status == "Sakit") {
            holder.tvStatus.text = "Sakit"
            holder.tvStatus.setTextColor(holder.itemView.context.resources.getColor(R.color.sakit_color))
        }

        // Mengatur aksi tombol edit dan hapus
        holder.btnEdit.setOnClickListener {
            onEditClick(data, position)
        }
        holder.btnDelete.setOnClickListener {
            onDeleteClick(position)
        }
    }

    // Mengembalikan jumlah item dalam daftar
    override fun getItemCount(): Int = dataList.size
}