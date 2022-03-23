package com.pamsimas.pamsimasapps.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Laporan(
    var laporanId: String? = null,
    var laporanDate: String? = null,
    var laporanPemasukan: Int? = null,
    var laporanPengeluaran: Int? = null
) : Parcelable