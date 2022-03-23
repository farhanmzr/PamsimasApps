package com.pamsimas.pamsimasapps.models

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Iuran(

    var iuranId: String ?= null,
    var customerId: String ?= null,
    var standAwal: String ?= null,
    var standAkhir: String ?= null,
    var pemakaian: String ?= null,
    var biayaAir: String ?= null,
    var pipa: String ?= null,
    var totalBiaya: String ?= null,
    var statusIuran: String ?= null,
    var dateInput: String ?= null


) : Parcelable