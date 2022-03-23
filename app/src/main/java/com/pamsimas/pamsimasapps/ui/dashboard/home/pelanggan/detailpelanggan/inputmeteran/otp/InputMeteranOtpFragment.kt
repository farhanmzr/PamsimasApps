package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.detailpelanggan.inputmeteran.otp

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.FirebaseFirestore
import com.pamsimas.pamsimasapps.databinding.FragmentInputMeteranOtpBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.models.Iuran
import com.pamsimas.pamsimasapps.models.Meteran
import com.pamsimas.pamsimasapps.models.Pamsimas
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.PelangganActivity
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.PelangganViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.detailpelanggan.inputmeteran.InputMeteranTwoFragment
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.tambahpelanggan.otp.TambahPelangganOtpFragment
import com.pamsimas.pamsimasapps.utils.AppConstants
import com.pamsimas.pamsimasapps.utils.AppConstants.BELUM_BAYAR
import com.pamsimas.pamsimasapps.utils.AppConstants.STATUS_SUCCESS
import com.pamsimas.pamsimasapps.utils.PriceFormatHelper
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class InputMeteranOtpFragment : Fragment() {

    private lateinit var binding: FragmentInputMeteranOtpBinding

    private val pelangganViewModel: PelangganViewModel by activityViewModels()

    private lateinit var meteranData: Meteran
    private lateinit var customersData: Customers
    private lateinit var iuranData: Iuran
    private lateinit var pamsimasData: Pamsimas

    private lateinit var progressDialog : Dialog

    private var pinAdmin: String? = null
    private var pipa: Int? = null
    private var biayaPemakaian: Int? = null

    companion object {
        const val EXTRA_METERAN_DATA = "extra_meteran_data"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputMeteranOtpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        progressDialog.show()

        iuranData = Iuran()
        customersData = Customers()
        meteranData = Meteran()

        val bundle = arguments
        if (bundle != null) {
            meteranData = bundle.getParcelable(InputMeteranTwoFragment.EXTRA_METERAN_DATA)!!
        }

        getCustomerData()
        getDataPamsimas()
        progressDialog.dismiss()

        val preferences = requireActivity().getSharedPreferences(TambahPelangganOtpFragment.PREFS_NAME, Context.MODE_PRIVATE)
        pinAdmin = preferences.getString("pinAdmin", "none")
        Log.e("pinAdmin", pinAdmin.toString())

        binding.apply {
            icBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
            txtPinEntry.setOnPinEnteredListener { str ->
                val otp = str.toString()
                if (otp == pinAdmin) {
                    progressDialog.show()
                    uploadImageMeteran()
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Pin yang dimasukkan tidak valid.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    }

    private fun getDataPamsimas() {
        pelangganViewModel.getDataPamsimas()
            .observe(viewLifecycleOwner) { pamsimas ->
                if (pamsimas != null) {
                    pamsimasData = pamsimas
                    pipa = pamsimasData.pipa?.toInt()
                    biayaPemakaian = pamsimasData.biayaPemakaian?.toInt()
                    Log.e("pipa", pipa.toString())
                    Log.e("biaya pemakaian", biayaPemakaian.toString())
                }
            }
    }

    private fun getCustomerData() {
        pelangganViewModel.getCustomerData()
            .observe(viewLifecycleOwner) { customersProfile ->
                if (customersProfile != null) {
                    customersData = customersProfile
                }
                Log.d("ViewModelCustomer: ", customersProfile.toString())
            }
    }

    private fun uploadImageMeteran() {
        pelangganViewModel.uploadImages(
            meteranData.uriPath!!,
            "${meteranData.meteranId}",
            "Images Meteran",
            "meteranPicture"
        ).observe(viewLifecycleOwner) { downloadUrl ->
            if (downloadUrl != null) {
                meteranData.meteranPicture = downloadUrl.toString()
                uploadMeteranData(meteranData)
            } else {
                progressDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    "Update Profile Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun uploadMeteranData(meteranData: Meteran) {
        pelangganViewModel.uploadMeteran(meteranData).observe(viewLifecycleOwner) { status ->
            if (status == STATUS_SUCCESS) {
                uploadIuranData(meteranData)
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadIuranData(meteranData: Meteran) {

        val pemakaian = meteranData.standAkhir!!.toInt() - meteranData.standAwal!!.toInt()

        val biayaAir = pemakaian * biayaPemakaian!!
        Log.e("Biaya AIR", biayaAir.toString())

        val totalBiaya = biayaAir + pipa!!
        Log.e("Total Biaya", totalBiaya.toString())

        val iuranInfo = Iuran(
            iuranId = meteranData.meteranId,
            customerId = meteranData.customerId,
            standAwal = meteranData.standAwal,
            standAkhir = meteranData.standAkhir,
            pemakaian = "$pemakaian kubik",
            biayaAir = biayaAir.toString(),
            pipa = pipa.toString(),
            totalBiaya = totalBiaya.toString(),
            statusIuran = BELUM_BAYAR,
            dateInput = meteranData.dateInput
        )
        pelangganViewModel.uploadIuran(iuranInfo).observe(viewLifecycleOwner) {status ->
            if (status == STATUS_SUCCESS) {
                progressDialog.dismiss()
                val intent =
                    Intent(requireActivity(), PelangganActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
                Toast.makeText(
                    requireContext(),
                    "Berhasil menginputkan meteran pelanggan",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
            }
        }

    }

}