package com.pamsimas.pamsimasapps.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pamsimas.pamsimasapps.data.FirebaseService
import com.pamsimas.pamsimasapps.models.Users
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class LoginViewModel: ViewModel() {

    private val firebaseServices: FirebaseService = FirebaseService()
    private var _usersProfile = MutableLiveData<Users>()

    fun createdNewUser(authUser: Users): LiveData<Users> =
        firebaseServices.createUserToFirestore(authUser)

    fun signInWithEmail(email: String, password: String): LiveData<Users> =
        firebaseServices.loginUser(email, password)

    fun forgotPasswordUser(email: String): LiveData<Users> =
        firebaseServices.forgotPasswordUser(email)

    //setMitraId
    fun setUsersProfile(userId: String): LiveData<Users> {
        _usersProfile = firebaseServices.getUserData(userId) as MutableLiveData<Users>
        return _usersProfile
    }

}