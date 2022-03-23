package com.pamsimas.pamsimasapps.ui.dashboard

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.pamsimas.pamsimasapps.data.FirebaseService
import com.pamsimas.pamsimasapps.models.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class MainViewModel: ViewModel() {

    private val firebaseServices: FirebaseService = FirebaseService()
    private var _usersProfile = MutableLiveData<Users>()
    private var _kegiatanData = MutableLiveData<Kegiatan>()
    private var _customersData = MutableLiveData<Customers>()
    private var _laporanData = MutableLiveData<Laporan>()
    private var _iuranData = MutableLiveData<Iuran>()

    //<<<<<<<<<<<<<<<<<<<<<<<<<< DATA ADMIN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //setUserId
    fun setUserProfile(userId: String): LiveData<Users> {
        _usersProfile = firebaseServices.getUserData(userId) as MutableLiveData<Users>
        return _usersProfile
    }

    fun getUserData(): LiveData<Users> {
        return _usersProfile
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<< DATA CUSTOMERS >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun setCustomerData(customerId: String): LiveData<Customers> {
        _customersData = firebaseServices.getCustomerData(customerId) as MutableLiveData<Customers>
        return _customersData
    }
    fun getCustomerData(): LiveData<Customers> {
        return _customersData
    }
    //Update Customer
    fun updateCustomerData(customerData: Customers): LiveData<Customers> =
        firebaseServices.updateCustomerData(customerData)

    //<<<<<<<<<<<<<<<<<<<<<<<<<< IURAN BULANAN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //setKegiatan
    fun setIuranData(customerId: String, iuranId: String): LiveData<Iuran> {
        _iuranData = firebaseServices.getIuranCustomerData(customerId, iuranId) as MutableLiveData<Iuran>
        return _iuranData
    }
    fun getIuranData(): LiveData<Iuran> {
        return _iuranData
    }
    //listIuran
    fun getListIuran(customerId: String): LiveData<List<Iuran>?> {
        return firebaseServices.getListIuranCustomer(customerId,"iuran").asLiveData()
    }
    //Update Iuran
    fun updateIuranData(iuranData: Iuran): LiveData<Iuran> =
        firebaseServices.updateIuranData(iuranData)

    //<<<<<<<<<<<<<<<<<<<<<<<<<< LAPORAN KEUANGAN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //Get List Laporan
    fun getListLaporanData(): LiveData<List<Laporan>?> {
        return firebaseServices.getListLaporanKeuangan("laporanKeuangan").asLiveData()
    }
    //Get Laporan
    fun setLaporanData(laporanId: String): LiveData<Laporan> {
        _laporanData = firebaseServices.getLaporanData(laporanId) as MutableLiveData<Laporan>
        return _laporanData
    }
    fun getLaporanData(): LiveData<Laporan> {
        return _laporanData
    }
    //Update Laporan
    fun updateLaporanData(laporanData: Laporan): LiveData<Laporan> =
        firebaseServices.updateLaporanKeuangan(laporanData)
    //uploadLaporanKeuangan
    fun uploadLaporanData(laporan: Laporan): LiveData<String> =
        firebaseServices.uploadLaporanKeuanganData(laporan)

    //<<<<<<<<<<<<<<<<<<<<<<<<<< RINGKASAN KEUANGAN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun uploadRingkasanData(laporanId: String, ringkasan: RingkasanLaporan): LiveData<String> =
        firebaseServices.uploadRingkasanLaporan(laporanId,ringkasan)
    //Get List Ringkasan Laporan
    fun getListRingkasanLaporanData(laporanId: String): LiveData<List<RingkasanLaporan>?> {
        return firebaseServices.getListRingkasanLaporan(laporanId,"ringkasanLaporan").asLiveData()
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<< KEGIATAN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //setKegiatan
    fun setKegiatanData(kegiatanId: String): LiveData<Kegiatan> {
        _kegiatanData = firebaseServices.getKegiatanData(kegiatanId) as MutableLiveData<Kegiatan>
        return _kegiatanData
    }
    fun getKegiatanData(): LiveData<Kegiatan> {
        return _kegiatanData
    }
    //listKegiatan
    fun getListKegiatan(): LiveData<List<Kegiatan>?> {
        return firebaseServices.getListKegiatan("kegiatan").asLiveData()
    }
    //uploadKegiatan
    fun uploadKegiatan(kegiatan: Kegiatan): LiveData<String> =
        firebaseServices.uploadKegiatan(kegiatan)

    //search
    fun getListSearch(querySearch: String): LiveData<List<Customers>?> {
        return firebaseServices.getSearchCustomers(querySearch, "customers").asLiveData()
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<< PROFILE >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //EditUser
    fun editUserData(authUser: Users): LiveData<Users> =
        firebaseServices.editUsersData(authUser)

    //uploadImages
    fun uploadImages(uri: Uri, uid: String, type: String, name: String): LiveData<String> =
        firebaseServices.uploadFiles(uri, uid, type, name)
}