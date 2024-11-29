package com.yonasputra.absensiswa


data class ModelDispen(
    val namaSiswa: String ?= null,
    val jamUTC: String ?= null,
    val kelas: String ?= null,
    val alasan: String ?= null,
    val jamKembali: String ?= null,
    var key: String? = null // Key akan diisi setelah data diambil
)
