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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentRegisterOneBinding
import com.pamsimas.pamsimasapps.databinding.FragmentRegisterTwoBinding
import com.pamsimas.pamsimasapps.models.Users
import com.pamsimas.pamsimasapps.ui.login.LoginActivity
import com.pamsimas.pamsimasapps.ui.login.LoginViewModel
import com.pamsimas.pamsimasapps.utils.DateHelper
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class RegisterTwoFragment : Fragment() {

    private lateinit var binding : FragmentRegisterTwoBinding

    private lateinit var mAuth: FirebaseAuth

    private val loginViewModel: LoginViewModel by activityViewModels()

    private lateinit var usersData: Users
    private lateinit var progressDialog : Dialog

    companion object {
        const val EXTRA_USER = "extra_user"
        const val PREFS_NAME = "user_pref"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRegisterTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        mAuth = Firebase.auth

        val bundle = arguments
        if (bundle != null) {
            usersData = bundle.getParcelable(EXTRA_USER)!!
        }

        binding.icBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }

        binding.btnRegister.setOnClickListener {
            if (validateName() && validatePIN()) {
                registerUser(usersData.email!!, usersData.password!!)
            }
        }


    }

    private fun registerUser(email: String, password: String) {
        progressDialog.show()

        val pamid = binding.etPamid.text.toString().trim()
        val nohp = binding.etNotelp.text.toString().trim()
        val pin = binding.etPin.text.toString().trim()

        val avatar = "https://firebasestorage.googleapis.com/v0/b/pamsimasapps.appspot.com/o/profile_default.png?alt=media&token=cd2245d2-fdb0-4016-8602-d03b397a533c"

        /*create a user*/
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = mAuth.currentUser
                    val userId = user!!.uid

                    val usersData = Users(
                        userId = userId,
                        userPicture = avatar,
                        name = usersData.name,
                        email = email,
                        pamid = pamid,
                        nohp = nohp,
                        pin = pin,
                        role = "Admin",
                        ttl = "-",
                        registeredAt = DateHelper.getCurrentDate(),
                        verified = false,
                    )
                    createNewUser(usersData)
                } else {
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            }
    }

    private fun createNewUser(usersData: Users) {
        Log.d("createdNewUser", usersData.name.toString())
        loginViewModel.createdNewUser(usersData).observe(viewLifecycleOwner) { newUser ->
            if (newUser.isCreated == true) {
                progressDialog.dismiss()
                Log.d(
                    ContentValues.TAG,
                    "Hello ${usersData.name}, Your Account Successfully Created!"
                )
                val user = mAuth.currentUser
//                Preference
//                val preferences = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
//                val editor = preferences.edit()
//                editor.putInt("totalShop", usersData.totalShop!!)
//                editor.apply()

                sendEmailVerification(user)
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser?) {
        user!!.sendEmailVerification()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // email sent
                    val dialog = AlertDialog.Builder(requireContext(), R.style.Theme_AppCompat_DayNight_Dialog).create()
                    dialog.setTitle("Registrasi Berhasil")
                    dialog.setMessage("Silahkan untuk verifikasi email anda agar dapat masuk ke Aplikasi Pamsimas.")
                    dialog.setButton(Dialog.BUTTON_POSITIVE, "Ok") { dialog, which ->
                        mAuth.signOut()
                        val intent =
                            Intent(requireActivity(), LoginActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    dialog.show()
                    dialog.setCancelable(false)
                    dialog.setCanceledOnTouchOutside(false)
                    Log.d(ContentValues.TAG, "Email sent.")
                }
            }
    }

    private fun validateName(): Boolean {

        val pamid = binding.etPamid.text.toString().trim()
        val nohp = binding.etNotelp.text.toString().trim()

        return when {
            pamid.isEmpty() -> {
                binding.etPamid.error = "Masukkan PAMID dengan benar."
                false
            }
            nohp.isEmpty() -> {
                binding.etNotelp.error = "Masukkan nomor telfon dengan benar."
                false
            }

            else -> {
                true
            }
        }

    }

    private fun validatePIN(): Boolean {

        val pin = binding.etPin.text.toString().trim()
        val confirmPIN = binding.etConfirmPin.text.toString().trim()

        return when {
            pin.length < 4 -> {
                binding.etPin.error = "Minimal PIN anda adalah 4 angka."
                false
            }
            pin != confirmPIN -> {
                Toast.makeText(requireContext(), "PIN tidak sama.", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }



}