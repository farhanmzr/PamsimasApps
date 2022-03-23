package com.pamsimas.pamsimasapps.data

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.pamsimas.pamsimasapps.models.*
import com.pamsimas.pamsimasapps.utils.AppConstants.BELUM_BAYAR
import com.pamsimas.pamsimasapps.utils.AppConstants.STATUS_SUCCESS
import com.pamsimas.pamsimasapps.utils.AppConstants.SUDAH_BAYAR
import com.pamsimas.pamsimasapps.utils.AppConstants.SUDAH_DIINPUT
import com.pamsimas.pamsimasapps.utils.DateHelper.getCurrentDate
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
class FirebaseService {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestoreRef: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val usersRef: CollectionReference = firestoreRef.collection("users")
    private val kegiatanRef: CollectionReference = firestoreRef.collection("kegiatan")
    private val customerRef: CollectionReference = firestoreRef.collection("customers")
    private val pamsimasRef: CollectionReference = firestoreRef.collection("data")
    private val laporanRef: CollectionReference = firestoreRef.collection("laporanKeuangan")

    private var STATUS_ERROR = "error"

    //<<<<<<<<<<<<<<<<<<<<<<<<<< AUTHENTICATION METHOD >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun createUserToFirestore(authUser: Users): LiveData<Users> {
        val createdMitraData = MutableLiveData<Users>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = usersRef.document(authUser.userId.toString())
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot? = task.result
                    if (document?.exists() == false) {
                        docRef.set(authUser).addOnCompleteListener {
                            if (it.isSuccessful) {
                                authUser.isCreated = true
                                createdMitraData.postValue(authUser)
                            } else {
                                Log.d(
                                    "errorCreateUser: ",
                                    it.exception?.message.toString()
                                )
                            }
                        }
                    }
                }
            }
                .addOnFailureListener {
                    Log.d("ErrorGetUser: ", it.message.toString())
                }
        }
        return createdMitraData
    }

    fun loginUser(email: String?, password: String?): LiveData<Users> {
        val authenticatedUser = MutableLiveData<Users>()
        CoroutineScope(Dispatchers.IO).launch {
            firebaseAuth.signInWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user: FirebaseUser? = firebaseAuth.currentUser
                        if (user != null) {
                            val uid = user.uid
                            val name = user.displayName
                            val email = user.email
                            val userInfo = Users(
                                userId = uid,
                                name = name,
                                email = email,
                                registeredAt = getCurrentDate()
                            )
                            authenticatedUser.postValue(userInfo)
                        }
                    } else {
                        Log.d("Error Authentication", "signInWithGoogle: ", task.exception)
                        val errorMessage = Users(
                            errorMessage = task.exception?.message
                        )
                        authenticatedUser.postValue(errorMessage)
                    }
                }
        }
        return authenticatedUser
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<< GET DATA FROM DATABASE METHOD >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun getUserData(userId: String): LiveData<Users> {
        val docRef: DocumentReference = usersRef.document(userId)
        val userProfileData = MutableLiveData<Users>()
        CoroutineScope(Dispatchers.IO).launch {
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val userProfile = document.toObject<Users>()
                    userProfileData.postValue(userProfile!!)
                    Log.d("getUserProfile: ", userProfile.toString())
                } else {
                    Log.d("Error getting Doc", "Document Doesn't Exist")
                }
            }
        }
        return userProfileData
    }

    fun getKegiatanData(kegiatanId: String): LiveData<Kegiatan> {
        val docRef: DocumentReference = kegiatanRef.document(kegiatanId)
        val kegiatanData = MutableLiveData<Kegiatan>()
        CoroutineScope(Dispatchers.IO).launch {
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val kegiatan = document.toObject<Kegiatan>()
                    kegiatanData.postValue(kegiatan!!)
                    Log.d("getKegiatanData: ", kegiatan.toString())
                } else {
                    Log.d("Error getting Doc", "Document Doesn't Exist")
                }
            }
        }
        return kegiatanData
    }

    fun getCustomerData(customerId: String): LiveData<Customers> {
        val docRef: DocumentReference = customerRef.document(customerId)
        val customerData = MutableLiveData<Customers>()
        CoroutineScope(Dispatchers.IO).launch {
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val customer = document.toObject<Customers>()
                    customerData.postValue(customer!!)
                    Log.d("getCustomerData: ", customer.toString())
                } else {
                    Log.d("Error getting Doc", "Document Doesn't Exist")
                }
            }
        }
        return customerData
    }

    fun getLaporanData(laporanId: String): LiveData<Laporan> {
        val docRef: DocumentReference = laporanRef.document(laporanId)
        val laporanData = MutableLiveData<Laporan>()
        CoroutineScope(Dispatchers.IO).launch {
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val laporan = document.toObject<Laporan>()
                    laporanData.postValue(laporan!!)
                    Log.d("getCustomerData: ", laporan.toString())
                } else {
                    Log.d("Error getting Doc", "Document Doesn't Exist")
                }
            }
        }
        return laporanData
    }

    fun getIuranCustomerData(customerId: String, iuranId: String): LiveData<Iuran> {
        val colRef: CollectionReference = customerRef.document(customerId).collection("iuran")
        val docRef: DocumentReference = colRef.document(iuranId)
        val iuranData = MutableLiveData<Iuran>()
        CoroutineScope(Dispatchers.IO).launch {
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val iuran = document.toObject<Iuran>()
                    iuranData.postValue(iuran!!)
                    Log.d("getIuranCustomerData: ", iuran.toString())
                } else {
                    Log.d("Error getting Doc", "Document Doesn't Exist")
                }
            }
        }
        return iuranData
    }

    fun getDataPamsimas(dataId: String): LiveData<Pamsimas> {
        val docRef: DocumentReference = pamsimasRef.document(dataId)
        val dataPamsimas = MutableLiveData<Pamsimas>()
        CoroutineScope(Dispatchers.IO).launch {
            docRef.get().addOnSuccessListener { document ->
                if (document != null) {
                    val data = document.toObject<Pamsimas>()
                    dataPamsimas.postValue(data!!)
                    Log.d("getDataPamsimas: ", data.toString())
                } else {
                    Log.d("Error getting Doc", "Document Doesn't Exist")
                }
            }
        }
        return dataPamsimas
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<< UPDATE DATABASE METHOD >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun editUsersData(authUser: Users): LiveData<Users> {
        val editedUsersData = MutableLiveData<Users>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = usersRef.document(authUser.userId.toString())
            docRef.set(authUser, SetOptions.merge()).addOnCompleteListener {
                if (it.isSuccessful) {
                    editedUsersData.postValue(authUser)
                } else {
                    Log.d(
                        "errorUpdateShop: ",
                        it.exception?.message.toString()
                    )
                }
            }
                .addOnFailureListener {
                    Log.d(
                        "errorCreateShop: ", it.message.toString()
                    )
                }
        }
        return editedUsersData
    }

    fun forgotPasswordUser(email: String?): LiveData<Users> {
        val editedUsersData = MutableLiveData<Users>()
        CoroutineScope(Dispatchers.IO).launch {
            firebaseAuth.sendPasswordResetEmail(email!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val userInfo = Users(
                            isResetPassword = true
                        )
                        editedUsersData.postValue(userInfo)
                    } else {
                        Log.d("Firebase Services", "Error Reset Password: ", task.exception)
                        val errorMessage = Users(
                            errorMessage = task.exception?.message
                        )
                        editedUsersData.postValue(errorMessage)
                    }
                }
        }
        return editedUsersData
    }

    fun updateIuranData(iuranData: Iuran): LiveData<Iuran> {
        val editIuran = MutableLiveData<Iuran>()
        CoroutineScope(Dispatchers.IO).launch {
            val colRef: CollectionReference = customerRef.document(iuranData.customerId.toString()).collection("iuran")
            val docRef: DocumentReference = colRef.document(iuranData.iuranId.toString())
            docRef.set(iuranData, SetOptions.merge()).addOnCompleteListener {
                if (it.isSuccessful) {
                    editIuran.postValue(iuranData)
                } else {
                    Log.d(
                        "errorUpdateCustomer: ",
                        it.exception?.message.toString()
                    )
                }
            }
                .addOnFailureListener {
                    Log.d(
                        "errorCreateUser: ", it.message.toString()
                    )
                }
        }
        return editIuran
    }

    fun updateLaporanKeuangan(laporan: Laporan): LiveData<Laporan> {
        val editLaporan = MutableLiveData<Laporan>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = laporanRef.document(laporan.laporanId.toString())
            docRef.set(laporan, SetOptions.merge()).addOnCompleteListener {
                if (it.isSuccessful) {
                    editLaporan.postValue(laporan)
                } else {
                    Log.d(
                        "errorUpdateLaporan: ",
                        it.exception?.message.toString()
                    )
                }
            }
                .addOnFailureListener {
                    Log.d(
                        "errorCreateUser: ", it.message.toString()
                    )
                }
        }
        return editLaporan
    }

    fun updateCustomerData(customerData: Customers): LiveData<Customers> {
        val editCustomers = MutableLiveData<Customers>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = customerRef.document(customerData.customerId.toString())
            docRef.set(customerData, SetOptions.merge()).addOnCompleteListener {
                if (it.isSuccessful) {
                    editCustomers.postValue(customerData)
                } else {
                    Log.d(
                        "errorUpdateCustomer: ",
                        it.exception?.message.toString()
                    )
                }
            }
                .addOnFailureListener {
                    Log.d(
                        "errorCreateUser: ", it.message.toString()
                    )
                }
        }
        return editCustomers
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<< POST DATA TO DATABASE METHOD >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun uploadFiles(uri: Uri, uid: String, type: String, name: String): LiveData<String> {
        val mStorage: FirebaseStorage = Firebase.storage
        val storageRef = mStorage.reference
        val fileRef = storageRef.child("$uid/$type/$name")
        val downloadUrl = MutableLiveData<String>()

        fileRef.putFile(uri).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            fileRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                downloadUrl.postValue(downloadUri.toString())
                Log.d("uploadFiles: ", downloadUri.toString())
            } else {
                task.exception?.let {
                    throw it
                }
            }
        }
        return downloadUrl
    }

    fun uploadKegiatan(kegiatan: Kegiatan): LiveData<String> {
        val statusNotif = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = kegiatanRef.document()
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot? = task.result
                    if (document?.exists() == false) {
                        kegiatan.kegiatanId = document.id
                        docRef.set(kegiatan).addOnCompleteListener {
                            if (it.isSuccessful) {
                                statusNotif.postValue(STATUS_SUCCESS)
                            } else {
                                STATUS_ERROR = it.exception?.message.toString()
                                statusNotif.postValue(STATUS_ERROR)
                                Log.d(
                                    "errorCreateKegiatan: ",
                                    it.exception?.message.toString()
                                )
                            }
                        }
                    }
                }
            }
                .addOnFailureListener {
                    STATUS_ERROR = it.message.toString()
                    statusNotif.postValue(STATUS_ERROR)
                    Log.d("ErrorUploadKegiatan: ", it.message.toString())
                }
        }
        return statusNotif
    }

    fun uploadCustomerData(customer: Customers): LiveData<String> {
        val statusUploadCustomerData = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = customerRef.document()
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot? = task.result
                    if (document?.exists() == false) {
                        customer.customerId = document.id
                        docRef.set(customer).addOnCompleteListener {
                            if (it.isSuccessful) {
                                statusUploadCustomerData.postValue(STATUS_SUCCESS)
                            } else {
                                STATUS_ERROR = it.exception?.message.toString()
                                statusUploadCustomerData.postValue(STATUS_ERROR)
                                Log.d(
                                    "errorCreateUser: ",
                                    it.exception?.message.toString()
                                )
                            }
                        }
                    }
                }
            }
                .addOnFailureListener {
                    STATUS_ERROR = it.message.toString()
                    statusUploadCustomerData.postValue(STATUS_ERROR)
                    Log.d("ErrorUploadNotif: ", it.message.toString())
                }
        }
        return statusUploadCustomerData
    }

    fun uploadLaporanKeuanganData(laporanKeuangan: Laporan): LiveData<String> {
        val statusUploadLaporan = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = laporanRef.document(laporanKeuangan.laporanId.toString())
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot? = task.result
                    if (document?.exists() == false) {
                        docRef.set(laporanKeuangan).addOnCompleteListener {
                            if (it.isSuccessful) {
                                statusUploadLaporan.postValue(STATUS_SUCCESS)
                            } else {
                                STATUS_ERROR = it.exception?.message.toString()
                                statusUploadLaporan.postValue(STATUS_ERROR)
                                Log.d(
                                    "errorCreateUser: ",
                                    it.exception?.message.toString()
                                )
                            }
                        }
                    }
                }
            }
                .addOnFailureListener {
                    STATUS_ERROR = it.message.toString()
                    statusUploadLaporan.postValue(STATUS_ERROR)
                    Log.d("ErrorUploadNotif: ", it.message.toString())
                }
        }
        return statusUploadLaporan
    }

    fun uploadIuranCustomer(iuran: Iuran): LiveData<String> {
        val statusUploadIuranCustomer = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val colRef: CollectionReference = customerRef.document(iuran.customerId.toString()).collection("iuran")
            val docRef: DocumentReference = colRef.document(iuran.iuranId!!)
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot? = task.result
                    if (document?.exists() == false) {
                        docRef.set(iuran).addOnCompleteListener {
                            if (it.isSuccessful) {
                                statusUploadIuranCustomer.postValue(STATUS_SUCCESS)
                            } else {
                                STATUS_ERROR = it.exception?.message.toString()
                                statusUploadIuranCustomer.postValue(STATUS_ERROR)
                                Log.d(
                                    "errorCreateUser: ",
                                    it.exception?.message.toString()
                                )
                            }
                        }
                    }
                }
            }
                .addOnFailureListener {
                    STATUS_ERROR = it.message.toString()
                    statusUploadIuranCustomer.postValue(STATUS_ERROR)
                    Log.d("ErrorUploadNotif: ", it.message.toString())
                }
        }
        return statusUploadIuranCustomer
    }

    fun uploadRingkasanLaporan(laporanId: String, ringkasan: RingkasanLaporan): LiveData<String> {
        val statusUploadRingkasanCustomer = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val colRef: CollectionReference = laporanRef.document(laporanId).collection("ringkasanLaporan")
            val docRef: DocumentReference = colRef.document()
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot? = task.result
                    if (document?.exists() == false) {
                        ringkasan.ringkasanId = document.id
                        docRef.set(ringkasan).addOnCompleteListener {
                            if (it.isSuccessful) {
                                statusUploadRingkasanCustomer.postValue(STATUS_SUCCESS)
                            } else {
                                STATUS_ERROR = it.exception?.message.toString()
                                statusUploadRingkasanCustomer.postValue(STATUS_ERROR)
                                Log.d(
                                    "errorCreateUser: ",
                                    it.exception?.message.toString()
                                )
                            }
                        }
                    }
                }
            }
                .addOnFailureListener {
                    STATUS_ERROR = it.message.toString()
                    statusUploadRingkasanCustomer.postValue(STATUS_ERROR)
                    Log.d("ErrorUploadNotif: ", it.message.toString())
                }
        }
        return statusUploadRingkasanCustomer
    }

    fun uploadMeteranCustomer(meteran: Meteran): LiveData<String> {
        val statusUploadMeteran = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val colRef: CollectionReference = customerRef.document(meteran.customerId.toString()).collection("meteran")
            val docRef: DocumentReference = colRef.document(meteran.meteranId!!)
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document: DocumentSnapshot? = task.result
                    if (document?.exists() == false) {
                        docRef.set(meteran).addOnCompleteListener {
                            if (it.isSuccessful) {
                                customerRef.document(meteran.customerId.toString())
                                    .update("statusInput", SUDAH_DIINPUT)
                                    .addOnCompleteListener {
                                        statusUploadMeteran.postValue(STATUS_SUCCESS)
                                    }
                                    .addOnFailureListener { error ->
                                        STATUS_ERROR = error.message.toString()
                                        statusUploadMeteran.postValue(STATUS_ERROR)
                                        Log.d("ErrorUpdateInput: ", error.message.toString())
                                    }
                            } else {
                                STATUS_ERROR = it.exception?.message.toString()
                                statusUploadMeteran.postValue(STATUS_ERROR)
                                Log.d(
                                    "errorCreateUser: ",
                                    it.exception?.message.toString()
                                )
                            }
                        }
                    }
                }
            }
                .addOnFailureListener {
                    STATUS_ERROR = it.message.toString()
                    statusUploadMeteran.postValue(STATUS_ERROR)
                    Log.d("ErrorUploadOrder: ", it.message.toString())
                }
        }
        return statusUploadMeteran
    }


    //<<<<<<<<<<<<<<<<<<<<<<<<<< GET LIST DATA FROM DATABASE METHOD >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun getListIuranCustomer(customerId: String, collectionRef: String): Flow<List<Iuran>?> {

        return callbackFlow {

            val colRef: CollectionReference = customerRef.document(customerId).collection(collectionRef)
            val doc = colRef.whereEqualTo("statusIuran", SUDAH_BAYAR)
            val listenerRegistration =
                doc.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listIuran = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Iuran>()
                    }
                    trySend(listIuran).isSuccess
                    Log.d("Iuran", listIuran.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListIuran: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListPemakaianCustomer(customerId: String, collectionRef: String): Flow<List<Meteran>?> {

        return callbackFlow {

            val colRef: CollectionReference = customerRef.document(customerId).collection(collectionRef)
            val doc = colRef.whereEqualTo("customerId", customerId)
            val listenerRegistration =
                doc.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listMeteran = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Meteran>()
                    }
                    trySend(listMeteran).isSuccess
                    Log.d("Meteran", listMeteran.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListMeteran: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListLaporanKeuangan(collectionRef: String): Flow<List<Laporan>?> {

        return callbackFlow {

            val colRef: CollectionReference = firestoreRef.collection(collectionRef)
            val listenerRegistration =
                colRef.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listLaporan = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Laporan>()
                    }
                    trySend(listLaporan).isSuccess
                    Log.d("Shops", listLaporan.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListKegiatan: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListRingkasanLaporan(laporanId: String, collectionRef: String): Flow<List<RingkasanLaporan>?> {

        return callbackFlow {

            val colRef: CollectionReference = laporanRef.document(laporanId).collection(collectionRef)
            val listenerRegistration =
                colRef.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listRingkasanLaporan = querySnapshot?.documents?.mapNotNull {
                        it.toObject<RingkasanLaporan>()
                    }
                    trySend(listRingkasanLaporan).isSuccess
                    Log.d("Shops", listRingkasanLaporan.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListKegiatan: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListKegiatan(collectionRef: String): Flow<List<Kegiatan>?> {

        return callbackFlow {

            val colRef: CollectionReference = firestoreRef.collection(collectionRef)
            val listenerRegistration =
                colRef.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listKegiatan = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Kegiatan>()
                    }
                    trySend(listKegiatan).isSuccess
                    Log.d("Shops", listKegiatan.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListKegiatan: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getSearchCustomers(query: String, collectionRef: String): Flow<List<Customers>?> {

        return callbackFlow {

            val colRef: CollectionReference = firestoreRef.collection(collectionRef)
            val doc = colRef.whereEqualTo("customerName", query)
            val listenerRegistration =
                doc.
                addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listPelanggan = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Customers>()
                    }
                    trySend(listPelanggan).isSuccess
                    Log.d("Customers", listPelanggan.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListPelanggan: ")
                listenerRegistration.remove()
            }
        }
    }

    fun getListCustomerData(
        query: String?,
        sort: Boolean,
        collectionRef: String,
        ascending: String?,
        descending: String?,
        sudahBayar: String?,
        belumBayar: String?
    ): Flow<List<Customers>?> {

        return callbackFlow {

            val reference: CollectionReference = firestoreRef.collection(collectionRef)
            val query =  reference.whereEqualTo("statusInput", query)
            val listenerQuery = if (sort) {
                when {
                    ascending == "A sampai Z" -> {
                        query.orderBy("customerName", Query.Direction.ASCENDING)
                    }
                    descending == "Z sampai A" -> {
                        query.orderBy("customerName", Query.Direction.DESCENDING)
                    }
                    sudahBayar == "Sudah Dibayar" -> {
                        query.whereEqualTo("statusTagihan", sudahBayar)
                    }
                    belumBayar == "Belum Dibayar" -> {
                        query.whereEqualTo("statusTagihan", belumBayar)
                    }
                    else -> {
                        query
                    }
                }
            } else {
                query
            }
            val listenerRegistration =
                listenerQuery.addSnapshotListener { querySnapshot: QuerySnapshot?, firestoreException: FirebaseFirestoreException? ->
                    if (firestoreException != null) {
                        cancel(
                            message = "Error fetching posts",
                            cause = firestoreException
                        )
                        return@addSnapshotListener
                    }
                    val listPelanggan = querySnapshot?.documents?.mapNotNull {
                        it.toObject<Customers>()
                    }
                    offer(listPelanggan)
                    Log.d("CustomersList", listPelanggan.toString())
                }
            awaitClose {
                Log.d(ContentValues.TAG, "getListCustomers: ")
                listenerRegistration.remove()
            }
        }

    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<< DELETE DATA FROM DATABASE METHOD >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun deleteCustomer(customerId: String): LiveData<String> {
        val statusDelete = MutableLiveData<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val docRef: DocumentReference = customerRef.document(customerId)
            docRef.delete().addOnSuccessListener { statusDelete.postValue(STATUS_SUCCESS) }
                .addOnFailureListener { statusDelete.postValue(it.message) }
        }
        return statusDelete
    }



}