package com.pamsimas.pamsimasapps.models

import android.net.Uri
import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Kegiatan(

    var kegiatanId: String? = null,
    var kegiatanDate: String? = null,
    var kegiatanDesc: String? = null,
    var kegiatanName: String? = null,
    var kegiatanPicture: String? = null,

    @get:Exclude
    var uriPath: Uri? = null

) : Parcelable