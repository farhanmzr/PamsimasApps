package com.pamsimas.pamsimasapps.ui.dashboard.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.google.firebase.auth.FirebaseAuth
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentHomeBinding
import com.pamsimas.pamsimasapps.databinding.FragmentProfileBinding
import com.pamsimas.pamsimasapps.models.Users
import com.pamsimas.pamsimasapps.ui.dashboard.MainActivity
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.profile.editpin.EditPinFragment
import com.pamsimas.pamsimasapps.ui.dashboard.profile.editprofile.EditProfileFragment
import com.pamsimas.pamsimasapps.utils.loadImage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var usersDataProfile: Users

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDataUser()
    }

    private fun getDataUser() {
        mainViewModel.getUserData()
            .observe(viewLifecycleOwner) { userProfile ->
                if (userProfile != null) {
                    usersDataProfile = userProfile
                    initView(usersDataProfile)
                }
                Log.d("ViewModelMitraProfile: ", userProfile.toString())
            }
    }

    private fun initView(usersDataProfile: Users) {
        with(binding){
            imgProfile.loadImage(usersDataProfile.userPicture)
            tvNama.text = usersDataProfile.name
            tvRole.text = usersDataProfile.role + " Pamsimas"
            tvPamid.text = usersDataProfile.pamid
            tvTelp.text = usersDataProfile.nohp

            linearEditPin.setOnClickListener {
                editPin()
            }

            linearEditProfile.setOnClickListener {
                editProfile()
            }

            btnLogout.setOnClickListener {
                userLogout()
            }
        }

    }

    private fun editPin() {
        val mEditPinFragment = EditPinFragment()
        setCurrentFragment(mEditPinFragment)
    }

    private fun editProfile() {
        val mEditProfileFragment = EditProfileFragment()
        setCurrentFragment(mEditProfileFragment)
    }

    private fun setCurrentFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.host_fragment_activity_main, fragment, MainActivity.CHILD_FRAGMENT)
        }
    }

    private fun userLogout() {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()
    }

    override fun onResume() {
        super.onResume()
        getDataUser()
    }

}