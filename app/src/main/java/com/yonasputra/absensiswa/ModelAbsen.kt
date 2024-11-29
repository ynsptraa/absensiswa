package com.yonasputra.absensiswa


data class ModelAbsen(
    var namaSiswa: String = "",
    var kelas: String = "",
    var status: String = "",
    var jamUTC: String = "",
    var key: String? = null // Key akan diisi setelah data diambil
)
