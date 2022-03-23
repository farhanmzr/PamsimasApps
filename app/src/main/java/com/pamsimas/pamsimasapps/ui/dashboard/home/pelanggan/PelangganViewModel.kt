package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.pamsimas.pamsimasapps.data.FirebaseService
import com.pamsimas.pamsimasapps.models.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class PelangganViewModel: ViewModel() {

    private val firebaseServices: FirebaseService = FirebaseService()

    private var _customersData = MutableLiveData<Customers>()
    private var _pamsimasData = MutableLiveData<Pamsimas>()

    //<<<<<<<<<<<<<<<<<<<<<<<<<< PELANGGAN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    fun getListCustomers(
        statusInput: String,
        sort: Boolean,
        ascending: String?,
        descending: String?,
        sudahBayar: String?,
        belumBayar: String?
    ): LiveData<List<Customers>?> {
        return firebaseServices.getListCustomerData(statusInput, sort,"customers", ascending, descending, sudahBayar, belumBayar).asLiveData()
    }
    //Upload Customer
    fun uploadCustomers(customerData: Customers): LiveData<String> =
        firebaseServices.uploadCustomerData(customerData)
    //Get Data Customer
    fun setCustomerData(customerId: String): LiveData<Customers> {
        _customersData = firebaseServices.getCustomerData(customerId) as MutableLiveData<Customers>
        return _customersData
    }
    fun getCustomerData(): LiveData<Customers> {
        return _customersData
    }
    //Delete Customer
    fun deleteCustomerData(customerId: String): LiveData<String> =
        firebaseServices.deleteCustomer(customerId)
    //Update Customer
    fun updateCustomerData(customerData: Customers): LiveData<Customers> =
        firebaseServices.updateCustomerData(customerData)

    //<<<<<<<<<<<<<<<<<<<<<<<<<< METERAN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //uploadImages
    fun uploadImages(uri: Uri, uid: String, type: String, name: String): LiveData<String> =
        firebaseServices.uploadFiles(uri, uid, type, name)

    //uploadMeteran
    fun uploadMeteran(meteran: Meteran): LiveData<String> =
        firebaseServices.uploadMeteranCustomer(meteran)

    //riwayatPemakaian
    fun getRiwayatPemakaian(customerId: String): LiveData<List<Meteran>?> {
        return firebaseServices.getListPemakaianCustomer(customerId, "meteran").asLiveData()
    }

    //<<<<<<<<<<<<<<<<<<<<<<<<<< IURAN >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    //uploadIuran
    fun uploadIuran(iuran: Iuran): LiveData<String> =
        firebaseServices.uploadIuranCustomer(iuran)

    //Get Data Pamsimas
    fun setDataPamsimas(dataId: String): LiveData<Pamsimas> {
        _pamsimasData = firebaseServices.getDataPamsimas(dataId) as MutableLiveData<Pamsimas>
        return _pamsimasData
    }
    fun getDataPamsimas(): LiveData<Pamsimas> {
        return _pamsimasData
    }
}