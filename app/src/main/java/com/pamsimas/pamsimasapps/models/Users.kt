package com.pamsimas.pamsimasapps.models

import android.os.Parcelable
import com.google.firebase.firestore.Exclude
import kotlinx.parcelize.Parcelize

@Parcelize
data class Users(

    var userId: String ?= null,
    var userPicture: String ?= null,
    var name: String ?= null,
    var email: String ?= null,
    var pamid: String ?= null,
    var nohp: String ?= null,
    var pin: String ?= null,
    var role: String ?= null,
    var ttl: String ?= null,
    var registeredAt: String ?= null,
    var verified: Boolean? = null,

    @get:Exclude
    var isAuthenticated: Boolean? = null,
    @get:Exclude
    var isCreated: Boolean? = null,
    @get:Exclude
    var errorMessage: String? = null,
    @get:Exclude
    var password: String? = null,
    @get:Exclude
    var confirmPassword: String? = null,
    @get:Exclude
    var isResetPassword: Boolean? = null

) : Parcelable