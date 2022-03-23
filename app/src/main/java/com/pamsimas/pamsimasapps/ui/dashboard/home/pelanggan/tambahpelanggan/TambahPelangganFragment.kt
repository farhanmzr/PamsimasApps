package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.tambahpelanggan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentTambahPelangganBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.models.Users
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.PelangganViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.tambahpelanggan.otp.TambahPelangganOtpFragment
import com.pamsimas.pamsimasapps.ui.login.register.RegisterTwoFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class TambahPelangganFragment : Fragment() {

    private lateinit var binding: FragmentTambahPelangganBinding

    private var jenisKelamin: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTambahPelangganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

    }

    private fun initView() {
        binding.apply {
            rgGender.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rb_pria -> jenisKelamin = binding.rbPria.text.toString()
                    R.id.rb_wanita -> jenisKelamin = binding.rbWanita.text.toString()
                }
            }
            icBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
            btnTambahPelanggan.setOnClickListener {
                if (validateForm()){
                    tambahPelanggan()
                }
            }
        }
    }

    private fun tambahPelanggan() {

        val nama = binding.etNamaLengkap.text.toString().trim()
        val nik = binding.etNik.text.toString().trim()
        val nohp = binding.etNomorTelepon.text.toString().trim()
        val provinsi = binding.etProvinsi.text.toString().trim()
        val kotakab = binding.etKotakab.text.toString().trim()
        val alamat = binding.etAlamat.text.toString().trim()
        val kodepos = binding.etKodepos.text.toString().trim()

        val customersData = Customers(customerName = nama,
            customerNIK = nik,
            customerPhone = nohp,
            customerProvinsi = provinsi,
            customerKotaKab = kotakab,
            customerAlamat = alamat,
            customerGender = jenisKelamin,
            customerKodePos = kodepos)

        val mTambahPelangganOTP = TambahPelangganOtpFragment()
        val mBundle = Bundle()
        mBundle.putParcelable(TambahPelangganOtpFragment.EXTRA_CUSTOMER, customersData)
        mTambahPelangganOTP.arguments = mBundle

        val mFragmentManager = parentFragmentManager
        mFragmentManager.commit {
            addToBackStack(null)
            replace(
                R.id.host_fragment_activity_pelanggan,
                mTambahPelangganOTP
            )
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