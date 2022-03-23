package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.tambahpelanggan.otp

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentTambahPelangganBinding
import com.pamsimas.pamsimasapps.databinding.FragmentTambahPelangganOtpBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.PelangganActivity
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.PelangganViewModel
import com.pamsimas.pamsimasapps.ui.login.register.RegisterTwoFragment
import com.pamsimas.pamsimasapps.utils.AppConstants.BELUM_BAYAR
import com.pamsimas.pamsimasapps.utils.AppConstants.BELUM_DIINPUT
import com.pamsimas.pamsimasapps.utils.AppConstants.STATUS_SUCCESS
import com.pamsimas.pamsimasapps.utils.DateHelper
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TambahPelangganOtpFragment : Fragment() {

    private lateinit var binding: FragmentTambahPelangganOtpBinding

    private lateinit var customersData: Customers
    private val pelangganViewModel: PelangganViewModel by activityViewModels()

    private lateinit var progressDialog : Dialog

    private var pinAdmin: String? = null

    companion object {
        const val EXTRA_CUSTOMER = "extra_customer"
        const val PREFS_NAME = "admin_pref"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTambahPelangganOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())

        val bundle = arguments
        if (bundle != null) {
            customersData = bundle.getParcelable(EXTRA_CUSTOMER)!!
        }

        val preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        pinAdmin = preferences.getString("pinAdmin", "none")

        binding.icBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }

        binding.txtPinEntry.setOnPinEnteredListener { str ->
            val otp = str.toString()
            if (otp == pinAdmin) {
                processTambahPelanggan()
            } else {
                Toast.makeText(requireContext(), "Pin yang dimasukkan tidak valid.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun processTambahPelanggan() {
        progressDialog.show()

        customersData = Customers(
        customerId = customersData.customerId,
        customerName = customersData.customerName,
        customerNIK = customersData.customerNIK,
        customerPhone = customersData.customerPhone,
        customerGender = customersData.customerGender,
        customerProvinsi = customersData.customerProvinsi,
        customerKotaKab = customersData.customerKotaKab,
        customerAlamat = customersData.customerAlamat,
        customerKodePos = customersData.customerKodePos,
        statusInput = BELUM_DIINPUT,
        statusTagihan = BELUM_BAYAR,
        registeredAt = DateHelper.getCurrentDate()
        )
        uploadCustomersData(customersData)

    }

    private fun uploadCustomersData(customersData: Customers) {
        pelangganViewModel.uploadCustomers(customersData).observe(viewLifecycleOwner) { status ->
            if (status == STATUS_SUCCESS) {
                progressDialog.dismiss()
                val intent =
                    Intent(requireActivity(), PelangganActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                Toast.makeText(requireContext(), "Berhasil menambah pelanggan", Toast.LENGTH_SHORT).show()
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
            }
        }
    }

}