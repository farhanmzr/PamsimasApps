package com.pamsimas.pamsimasapps.ui.dashboard.profile.editprofile

import android.app.Dialog
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentEditProfileBinding
import com.pamsimas.pamsimasapps.models.Users
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.utils.DatePickerHelper
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import com.pamsimas.pamsimasapps.utils.getMonthString
import com.pamsimas.pamsimasapps.utils.loadImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
class EditProfileFragment : Fragment() {

    private lateinit var binding: FragmentEditProfileBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var usersDataProfile: Users
    private lateinit var progressDialog : Dialog

    private var uriImagePath: Uri? = null
    private lateinit var datePicker: DatePickerHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        datePicker = DatePickerHelper(requireContext())

        val bottomNav: BottomNavigationView =
            requireActivity().findViewById(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE

        getDataUser()

        val getImage =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uriImage ->
                if (uriImage.path != null) {
                    uriImagePath = uriImage
                    binding.imgProfile.setImageURI(uriImagePath)
                }
            }

        binding.apply {
            imgChangePicture.setOnClickListener {
                getImage.launch(arrayOf("image/*"))
            }

            icBack.setOnClickListener {
                bottomNav.visibility = View.VISIBLE
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }

            etTtl.setOnClickListener {
                openDateDialog()
            }
        }

    }

    private fun openDateDialog() {
        val cal = Calendar.getInstance()
        val d = cal.get(Calendar.DAY_OF_MONTH)
        val m = cal.get(Calendar.MONTH)
        val y = cal.get(Calendar.YEAR)
        datePicker.setMaxDate(cal.timeInMillis)
        datePicker.showDialog(d, m, y, object : DatePickerHelper.Callback {
            override fun onDateSelected(datePicker: View, dayofMonth: Int, month: Int, year: Int) {
                val dayStr = if (dayofMonth < 10) "0${dayofMonth}" else "$dayofMonth"
                val mon = getMonthString(month)
                val date = "${dayStr}/${mon}/${year}"
                binding.etTtl.setText(date)
            }
        })
    }

    private fun getDataUser() {
        mainViewModel.getUserData()
            .observe(viewLifecycleOwner) { usersProfile ->
                if (usersProfile != null) {
                    usersDataProfile = usersProfile
                    initView(usersDataProfile)
                }
                Log.d("ViewModelShopsProfile: ", usersProfile.toString())
            }
    }

    private fun initView(usersDataProfile: Users) {
        with(binding) {
            etNamaLengkap.setText(usersDataProfile.name)
            etNomorTelepon.setText(usersDataProfile.nohp)
            etPamid.setText(usersDataProfile.pamid)
            etTtl.setText(usersDataProfile.ttl)
            imgProfile.loadImage(usersDataProfile.userPicture)
            btnSimpan.setOnClickListener{
                if (validateInputEditProfile()) {
                    EditDataProfile()
                }
            }
        }
    }

    private fun EditDataProfile() {
        progressDialog.show()
        with(binding) {
            usersDataProfile.name = etNamaLengkap.text.toString()
            usersDataProfile.nohp = etNomorTelepon.text.toString()
            usersDataProfile.pamid = etPamid.text.toString()
            usersDataProfile.ttl = etTtl.text.toString()
        }

        if (uriImagePath != null) {
            uploadUsersPicture()
        } else {
            updateUsersData()
        }
    }

    //Upload Image User
    private fun uploadUsersPicture() {
        mainViewModel.uploadImages(
            uriImagePath!!,
            "${usersDataProfile.userId.toString()}${usersDataProfile.name}",
            "Images",
            "profilePicture"
        ).observe(viewLifecycleOwner) { downloadUrl ->
            if (downloadUrl != null) {
                usersDataProfile.userPicture = downloadUrl.toString()
                updateUsersData()
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
    //Update User Data
    private fun updateUsersData() {
        mainViewModel.editUserData(usersDataProfile).observe(viewLifecycleOwner) { updateData ->
            if (updateData != null) {
                progressDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    "Profil Data User Successfull Updated",
                    Toast.LENGTH_SHORT
                ).show()
                val bottomNav: BottomNavigationView =
                    requireActivity().findViewById(R.id.bottom_navigation)
                bottomNav.visibility = View.VISIBLE
                requireActivity().supportFragmentManager.popBackStackImmediate()
            } else {
                progressDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    "Update Data Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validateInputEditProfile(): Boolean {

        val name = binding.etNamaLengkap.text.toString().trim()
        val nohp = binding.etNomorTelepon.text.toString().trim()
        val pamid = binding.etPamid.text.toString().trim()
        val ttl = binding.etTtl.text.toString().trim()

        return when {
            name.isEmpty() -> {
                binding.etNamaLengkap.error = "Nama Lengkap tidak boleh kosong."
                false
            }
            nohp.isEmpty() -> {
                binding.etNomorTelepon.error = "Nomor Telfon tidak boleh kosong."
                false
            }
            pamid.isEmpty() -> {
                binding.etPamid.error = "Pam ID tidak boleh kosong."
                false
            }
            ttl.isEmpty() -> {
                binding.etTtl.error = "Tempat Tanggal Lahir tidak boleh kosong."
                false
            }
            else -> {
                true
            }
        }

    }


}