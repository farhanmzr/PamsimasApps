package com.pamsimas.pamsimasapps.ui.dashboard.profile.editpin

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentEditPinBinding
import com.pamsimas.pamsimasapps.models.Users
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import com.pamsimas.pamsimasapps.utils.loadImage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class EditPinFragment : Fragment() {

    private lateinit var binding: FragmentEditPinBinding

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var usersDataProfile: Users
    private lateinit var progressDialog : Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditPinBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())

        val bottomNav: BottomNavigationView =
            requireActivity().findViewById(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE

        binding.icBack.setOnClickListener {
            bottomNav.visibility = View.VISIBLE
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }

        getDataUser()
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
            etPinLama.setText(usersDataProfile.pin)
            btnSimpan.setOnClickListener{
                if (validateInputEditPin()) {
                    EditDataPin()
                }
            }
        }
    }

    private fun EditDataPin() {
        progressDialog.show()
        if (usersDataProfile.pin == binding.etPinLama.text.toString()){
            val newPin = binding.etPinBaru.text.toString()
            updatePinUser(newPin)
        } else {
            progressDialog.dismiss()
            Toast.makeText(
                requireActivity(),
                "Pin Lama salah/tidak valid",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updatePinUser(newPin: String) {
        usersDataProfile.pin = newPin
        mainViewModel.editUserData(usersDataProfile).observe(viewLifecycleOwner) { updateData ->
            if (updateData != null) {
                progressDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    "Pin User Successfull Updated",
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
                    "Update Pin Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validateInputEditPin(): Boolean {

        val oldPin = binding.etPinLama.text.toString().trim()
        val newPin = binding.etPinBaru.text.toString().trim()
        val confirmPin = binding.etConfirmPinBaru.text.toString().trim()

        return when {
            oldPin.isEmpty() -> {
                binding.etPinLama.error = "PIN lama tidak boleh kosong."
                false
            }
            newPin.isEmpty() -> {
                binding.etPinBaru.error = "PIN baru tidak boleh kosong."
                false
            }
            newPin.length < 4 -> {
                binding.etPinBaru.error = "Minimal PIN adalah 4 angka."
                false
            }
            confirmPin.isEmpty() -> {
                binding.etConfirmPinBaru.error = "Konfirmasi PIN tidak boleh kosong."
                false
            }
            confirmPin.length < 4 -> {
                binding.etConfirmPinBaru.error = "Minimal PIN adalah 4 angka."
                false
            }
            newPin != confirmPin -> {
                Toast.makeText(requireContext(), "PIN baru tidak sama.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }

}