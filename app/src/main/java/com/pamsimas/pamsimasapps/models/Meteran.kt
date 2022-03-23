package com.pamsimas.pamsimasapps.models

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Meteran(

    var meteranId: String ?= null,
    var customerId: String ?= null,
    var standAwal: String ?= null,
    var standAkhir: String ?= null,
    var meteranSegel: String ?= null,
    var meteranDesc: String ?= null,
    var meteranPicture: String ?= null,

    var dateInput: String ?= null,

    @get:Exclude
    var uriPath: Uri? = null

) : Parcelable