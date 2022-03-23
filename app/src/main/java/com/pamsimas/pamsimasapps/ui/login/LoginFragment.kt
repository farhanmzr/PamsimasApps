package com.pamsimas.pamsimasapps.ui.login

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.preference.Preference
import com.google.firebase.auth.FirebaseAuth
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentLoginBinding
import com.pamsimas.pamsimasapps.databinding.ItemDialogLoginFailedBinding
import com.pamsimas.pamsimasapps.models.Users
import com.pamsimas.pamsimasapps.ui.dashboard.MainActivity
import com.pamsimas.pamsimasapps.ui.login.register.RegisterOneFragment
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper.Companion.progressDialog
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding

    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var users: Users

    private lateinit var progressDialog : Dialog

    companion object {
        const val PREFS_NAME = "admin_pref"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = progressDialog(requireContext())

        binding.apply {
            btnLogin.setOnClickListener {
                if (validateEmail() && validatePassword()) {
                    userLogin()
                }
            }

            tvDaftar.setOnClickListener {
                gotoRegister()
            }
            tvForgotPassword.setOnClickListener {
                gotoForgotPassword()
            }
        }

    }

    private fun userLogin() {

        progressDialog.show()

        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        loginViewModel.signInWithEmail(email, password)
            .observe(viewLifecycleOwner) { usersData ->
                if (usersData != null && usersData.errorMessage == null) {
                    // Login sukses, masuk ke Main Activity
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user!!.isEmailVerified) {
                        progressDialog.dismiss()
                        initAdmin(usersData)
                        gotoMainActivity(usersData)
                    } else {
                        progressDialog.dismiss()
                        FirebaseAuth.getInstance().signOut()
                        Toast.makeText(requireContext(), "Silahkan verifikasi email anda.", Toast.LENGTH_SHORT).show()
                        //restart this activity
                    }
                } else if (usersData.errorMessage != null) {
                    progressDialog.dismiss()
                    val dialog = Dialog(requireContext())
                    val binding: ItemDialogLoginFailedBinding =
                        ItemDialogLoginFailedBinding.inflate(LayoutInflater.from(context))
                    dialog.setContentView(binding.root)
                    binding.btnRegister.setOnClickListener {
                        gotoRegister()
                        dialog.dismiss()
                    }
                    dialog.show()
                }
            }
    }

    private fun initAdmin(usersData: Users) {
        loginViewModel.setUsersProfile(usersData.userId.toString()).observe(viewLifecycleOwner) { userProfile ->
            if (userProfile != null) {
                Log.e(usersData.role.toString(), "roleUser")
                Log.e("userId", usersData.userId.toString())
                users = userProfile
                //Preference
                val preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val pinAdmin = userProfile.pin
                val editor = preferences.edit()
                editor.putString("pinAdmin", pinAdmin)
                editor.apply()
                Log.e(pinAdmin, "pinAdmin")
            }
        }
    }

    private fun gotoForgotPassword() {
        val forgotPasswordFragment = ForgotPasswordFragment()
        val mFragmentManager = parentFragmentManager
        mFragmentManager.commit {
            addToBackStack(null)
            replace(
                R.id.host_login_activity,
                forgotPasswordFragment
            )
        }
    }

    private fun gotoRegister() {
        val registerFragment = RegisterOneFragment()
        val mFragmentManager = parentFragmentManager
        mFragmentManager.commit {
            addToBackStack(null)
            replace(
                R.id.host_login_activity,
                registerFragment
            )
        }
    }

    private fun gotoMainActivity(usersData: Users) {
        val intent =
            Intent(requireActivity(), MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_USER, usersData)
        startActivity(intent)
        requireActivity().finish()
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

        return when {
            password.length < 6 -> {
                binding.etPassword.error = "Masukkan Kata Sandi dengan benar"
                false
            }
            else -> {
                true
            }
        }
    }

}