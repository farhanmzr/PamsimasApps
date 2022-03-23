package com.pamsimas.pamsimasapps.ui.login.register

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentRegisterOneBinding
import com.pamsimas.pamsimasapps.models.Users
import com.pamsimas.pamsimasapps.ui.login.LoginActivity
import com.pamsimas.pamsimasapps.ui.login.LoginViewModel
import com.pamsimas.pamsimasapps.utils.DateHelper
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class RegisterOneFragment : Fragment() {

    private lateinit var binding : FragmentRegisterOneBinding

    private lateinit var progressDialog : Dialog

    companion object {
        const val PREFS_NAME = "user_pref"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterOneBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())


        binding.tvLogin.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }

        binding.btnNext.setOnClickListener {
            if (validateName() && validateEmail() && validatePassword()) {
                registerUser()
            }
        }

    }

    private fun registerUser() {

        val name = binding.etNamaLengkap.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        val usersData = Users(name = name,
            email = email,
            password = password,
            confirmPassword = confirmPassword)

        val mRegisterTwoFragment = RegisterTwoFragment()
        val mBundle = Bundle()
        mBundle.putParcelable(RegisterTwoFragment.EXTRA_USER, usersData)
        mRegisterTwoFragment.arguments = mBundle

        val mFragmentManager = parentFragmentManager
        mFragmentManager.commit {
            addToBackStack(null)
            replace(
                R.id.host_login_activity,
                mRegisterTwoFragment
            )
        }

    }

    private fun validateName(): Boolean {

        val name = binding.etNamaLengkap.text.toString().trim()

        return when {
            name.length < 3 -> {
                binding.etNamaLengkap.error = "Masukkan nama dengan benar."
                false
            }

            else -> {
                true
            }
        }

    }

    private fun validateEmail(): Boolean {

        val email = binding.etEmail.text.toString().trim()

        return when {
            !email.contains("@") && !email.contains(".") -> {
                binding.etEmail.error = "Masukkan Email yang Valid."
                false
            }
            !email.contains("@") -> {
                binding.etEmail.error = "Masukkan Email yang Valid."
                false
            }
            !email.contains(".") -> {
                binding.etEmail.error = "Masukkan Email yang Valid."
                false
            }
            email.contains(" ") -> {
                binding.etEmail.error = "Masukkan Email yang Valid."
                false
            }
            email.length < 6 -> {
                binding.etEmail.error = "Masukkan Email yang Valid."
                false
            }
            else -> {
                true
            }
        }
    }

    private fun validatePassword(): Boolean {

        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        return when {
            password.length < 6 -> {
                binding.etPassword.error = "Minimal Kata Sandi adalah 6 huruf."
                false
            }
            password != confirmPassword -> {
                Toast.makeText(requireContext(), "Kata Sandi tidak sama.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }

}