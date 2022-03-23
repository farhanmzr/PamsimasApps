package com.pamsimas.pamsimasapps.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Customers(

    var customerId: String ?= null,
    var customerName: String ?= null,
    var customerNIK: String ?= null,
    var customerPhone: String ?= null,
    var customerGender: String ?= null,
    var customerProvinsi: String ?= null,
    var customerKotaKab: String ?= null,
    var customerAlamat: String ?= null,
    var customerKodePos: String? = null,
    var statusInput: String? = null,
    var statusTagihan: String? = null,

    var registeredAt: String ?= null

) : Parcelable