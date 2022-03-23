package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.editpelanggan

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentEditPelangganBinding
import com.pamsimas.pamsimasapps.databinding.FragmentTambahPelangganBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.PelangganViewModel
import com.pamsimas.pamsimasapps.utils.AppConstants
import com.pamsimas.pamsimasapps.utils.AppConstants.PRIA
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class EditPelangganFragment : Fragment() {

    private lateinit var binding: FragmentEditPelangganBinding

    private val pelangganViewModel: PelangganViewModel by activityViewModels()
    private lateinit var customerData: Customers
    private lateinit var progressDialog : Dialog

    private var jenisKelamin: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPelangganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = ProgressDialogHelper.progressDialog(requireContext())

        getCustomerData()
    }

    private fun getCustomerData() {
        pelangganViewModel.getCustomerData()
            .observe(viewLifecycleOwner) { customers ->
                if (customers != null) {
                    customerData = customers
                    setViewCustomerData(customerData)
                }
            }
    }

    private fun setViewCustomerData(customerData: Customers) {
        with(binding){
            rgGender.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rb_pria -> jenisKelamin = binding.rbPria.text.toString()
                    R.id.rb_wanita -> jenisKelamin = binding.rbWanita.text.toString()
                }
            }
            icBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
            //Inputan
            etNamaLengkap.setText(customerData.customerName)
            etAlamat.setText(customerData.customerAlamat)
            if (customerData.customerGender == PRIA){
                rbPria.isChecked = true
            } else {
                rbWanita.isChecked = true
            }
            etKotakab.setText(customerData.customerKotaKab)
            etKodepos.setText(customerData.customerKodePos)
            etNik.setText(customerData.customerNIK)
            etNomorTelepon.setText(customerData.customerPhone)
            etProvinsi.setText(customerData.customerProvinsi)

            btnEditPelanggan.setOnClickListener {
                if (validateForm()){
                    editPelanggan()
                }
            }

        }
    }

    private fun editPelanggan() {
        progressDialog.show()
        customerData.customerName = binding.etNamaLengkap.text.toString()
        customerData.customerNIK = binding.etNik.text.toString()
        customerData.customerPhone = binding.etNomorTelepon.text.toString()
        customerData.customerProvinsi = binding.etProvinsi.text.toString()
        customerData.customerKotaKab = binding.etKotakab.text.toString()
        customerData.customerAlamat = binding.etAlamat.text.toString()
        customerData.customerKodePos = binding.etKodepos.text.toString()
        customerData.customerGender = jenisKelamin
        updateCustomer(customerData)
    }

    private fun updateCustomer(customerData: Customers) {
        pelangganViewModel.updateCustomerData(customerData).observe(viewLifecycleOwner) { updateStatus ->
            if (updateStatus!=null){
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), "Berhasil mengedit pelanggan.", Toast.LENGTH_LONG).show()
                requireActivity().supportFragmentManager.popBackStackImmediate()
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), "Gagal mengedit pelanggan.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateForm(): Boolean {

        val nama = binding.etNamaLengkap.text.toString().trim()
        val nik = binding.etNik.text.toString().trim()
        val nohp = binding.etNomorTelepon.text.toString().trim()
        val provinsi = binding.etProvinsi.text.toString().trim()
        val kotakab = binding.etKotakab.text.toString().trim()
        val alamat = binding.etAlamat.text.toString().trim()
        val kodepos = binding.etKodepos.text.toString().trim()

        return when {
            nama.isEmpty() -> {
                binding.etNamaLengkap.error = "Nama lengkap tidak boleh kosong"
                false
            }
            nik.isEmpty() -> {
                binding.etNik.error = "NIK tidak boleh kosong"
                false
            }
            nohp.isEmpty() -> {
                binding.etNomorTelepon.error = "Nomor HP tidak boleh kosong"
                false
            }
            provinsi.isEmpty() -> {
                binding.etProvinsi.error = "Provinsi tidak boleh kosong"
                false
            }
            kotakab.isEmpty() -> {
                binding.etKotakab.error = "Kota/kabupaten tidak boleh kosong"
                false
            }
            alamat.isEmpty() -> {
                binding.etAlamat.error = "Alamat tidak boleh kosong"
                false
            }
            kodepos.isEmpty() -> {
                binding.etKodepos.error = "Kode Pos tidak boleh kosong"
                false
            }
            jenisKelamin.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Jenis Kelamin tidak boleh kosong", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }

}