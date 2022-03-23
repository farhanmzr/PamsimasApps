package com.pamsimas.pamsimasapps.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RingkasanLaporan(
    var ringkasanId: String? = null,
    var ringkasanKategori: String? = null,
    var ringkasanKeterangan: String? = null,
    var ringkasanPrice: Int? = null,
    var ringkasanTipe: String? = null
) : Parcelable