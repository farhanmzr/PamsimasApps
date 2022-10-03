package com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas.tambahkegiatan

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentTambahKegiatanPamsimasBinding
import com.pamsimas.pamsimasapps.models.Kegiatan
import com.pamsimas.pamsimasapps.ui.dashboard.MainActivity
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas.KegiatanPamsimasFragment
import com.pamsimas.pamsimasapps.utils.AppConstants
import com.pamsimas.pamsimasapps.utils.DatePickerHelper
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
class TambahKegiatanPamsimasFragment : Fragment() {

    private lateinit var binding : FragmentTambahKegiatanPamsimasBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var kegiatanData: Kegiatan
    private var uriImagePath: Uri? = null

    private lateinit var progressDialog : Dialog
    private lateinit var datePicker : DatePickerHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTambahKegiatanPamsimasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        kegiatanData = Kegiatan()
        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        datePicker = DatePickerHelper(requireContext())

        initView()
    }

    private fun initView() {
        val getImage =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uriImage ->
                if (uriImage.path != null) {
                    uriImagePath = uriImage
                    binding.imgKegiatan.setImageURI(uriImagePath)
                    binding.linearImgChange.visibility = View.GONE
                }
            }

        binding.icBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }

        binding.etDateKegiatan.setOnClickListener {
            openDateDialog()
        }

        binding.tvUnggahFoto.setOnClickListener {
            getImage.launch(arrayOf("image/*"))
        }

        binding.btnTambahKegiatan.setOnClickListener {
            if (uriImagePath == null) {
                Toast.makeText(requireContext(), "Gambar Kegiatan tidak boleh kosong.", Toast.LENGTH_SHORT).show()
            }
            if (validateInput()) {
                uploadImageData()
            }
        }

    }

    private fun openDateDialog() {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)
        datePicker.showDialog(d, m, y, object : DatePickerHelper.Callback {
            override fun onDateSelected(datePicker: View, dayofMonth: Int, month: Int, year: Int) {
                val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "$dayofMonth"
                val mon = month + 1
                val monthStr = if (mon < 10) "0${mon}" else "$mon"
                val date = "${dayStr}/${monthStr}/${year}"
                binding.etDateKegiatan.setText(date)
            }
        })
    }

    private fun uploadImageData() {

        progressDialog.show()

        kegiatanData = Kegiatan(
            kegiatanPicture = kegiatanData.kegiatanPicture,
            kegiatanName = binding.etNamaKegiatan.text.toString(),
            kegiatanDate = binding.etDateKegiatan.text.toString(),
            kegiatanDesc = binding.etDescKegiatan.text.toString()
        )

        mainViewModel.uploadImages(
            uriImagePath!!,
            "${kegiatanData.kegiatanName}",
            "Images",
            "KegiatanPicture"
        ).observe(viewLifecycleOwner) { downloadUrl ->
            if (downloadUrl != null) {
                Toast.makeText(requireContext(), "Upload image success", Toast.LENGTH_SHORT).show()
                kegiatanData.kegiatanPicture = downloadUrl.toString()
                uploadKegiatanData(kegiatanData)
            } else {
                progressDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    "Upload image Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun uploadKegiatanData(kegiatanData: Kegiatan) {
        mainViewModel.uploadKegiatan(kegiatanData).observe(viewLifecycleOwner) { status ->
            if (status == AppConstants.STATUS_SUCCESS) {
                progressDialog.dismiss()
                val mKegiatanPamsimasFragment = KegiatanPamsimasFragment()
                setCurrentFragment(mKegiatanPamsimasFragment)
                Toast.makeText(
                    requireContext(),
                    "Selamat anda telah berhasil menambahkan kegiatan pamsimas",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                progressDialog.dismiss()
                Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.host_fragment_activity_main, fragment)
        }
    }

    private fun validateInput(): Boolean {

        val kegiatanName = binding.etNamaKegiatan.text.toString().trim()
        val kegiatanDate = binding.etDateKegiatan.text.toString().trim()
        val kegiatanDesc = binding.etDescKegiatan.text.toString().trim()

        return when {
            kegiatanName.isEmpty() -> {
                binding.etNamaKegiatan.error = "Nama Kegiatan tidak boleh kosong"
                false
            }
            kegiatanDate.isEmpty() -> {
                binding.etDateKegiatan.error = "Nama Kegiatan tidak boleh kosong."
                false
            }
            kegiatanDesc.length < 10 -> {
                binding.etDescKegiatan.error = "Minimal Deskripsi Kegiatan adalah 10 huruf."
                false
            }
            else -> {
                true
            }
        }
    }

}