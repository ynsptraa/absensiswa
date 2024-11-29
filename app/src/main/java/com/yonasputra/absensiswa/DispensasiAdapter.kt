package com.yonasputra.absensiswa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DispensasiAdapter(
    private val dataList: ArrayList<ModelDispen>,
    private val onEditClick: (ModelDispen, Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<DispensasiAdapter.DispensasiViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DispensasiViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dispensasi, parent, false)
        return DispensasiViewHolder(view)
    }

    override fun onBindViewHolder(holder: DispensasiViewHolder, position: Int) {
        val data = dataList[position]

        holder.tvNama.text = data.namaSiswa ?: "Nama Tidak Tersedia"
        holder.tvTanggal.text = "Jam Dispen : "+data.jamUTC ?: "Tanggal Tidak Tersedia"
        holder.tvKelas.text = data.kelas ?: "Kelas Tidak Tersedia"
        holder.tvAlasan.text = data.alasan ?: "Alasan Tidak Tersedia"
        holder.tvJamKembali.text = "Jam Kembali : "+data.jamKembali ?: "Jam Kembali Tidak Tersedia"

        holder.btnEdit.setOnClickListener {
            onEditClick(data, position)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int = dataList.size

    class DispensasiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNama: TextView = itemView.findViewById(R.id.tvNamaSiswa)
        val tvTanggal: TextView = itemView.findViewById(R.id.tvJamUTC)
        val tvKelas: TextView = itemView.findViewById(R.id.tvKelas)
        val tvAlasan: TextView = itemView.findViewById(R.id.tvAlasan)
        val tvJamKembali: TextView = itemView.findViewById(R.id.tvJamKembali)
        val btnEdit: Button = itemView.findViewById(R.id.btn_edit)
        val btnDelete: Button = itemView.findViewById(R.id.btn_hapus)
    }
}

