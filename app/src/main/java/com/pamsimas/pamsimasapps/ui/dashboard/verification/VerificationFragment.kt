package com.pamsimas.pamsimasapps.ui.dashboard.verification

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentVerificationBinding
import com.pamsimas.pamsimasapps.models.Users
import com.pamsimas.pamsimasapps.ui.dashboard.MainActivity
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.HomeFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi


@ExperimentalCoroutinesApi
class VerificationFragment : Fragment() {

    private lateinit var binding : FragmentVerificationBinding

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var usersDataProfile: Users

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav: BottomNavigationView =
            requireActivity().findViewById(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE

    }

    override fun onResume() {
        super.onResume()
        updateDataMitra()
    }

    override fun onPause() {
        super.onPause()
        updateDataMitra()
    }

    override fun onStop() {
        super.onStop()
        updateDataMitra()
    }

    private fun updateDataMitra() {
        mainViewModel.getUserData()
            .observe(viewLifecycleOwner, { userProfile ->
                if (userProfile != null) {
                    usersDataProfile = userProfile
                    if (usersDataProfile.verified == true) {
                        val mFragmentManager = parentFragmentManager
                        val mHomeFragment = HomeFragment()
                        mFragmentManager.commit {
                            addToBackStack(null)
                            replace(
                                R.id.host_fragment_activity_main,
                                mHomeFragment,
                                MainActivity.CHILD_FRAGMENT
                            )
                        }
                        val bottomNav: BottomNavigationView =
                            requireActivity().findViewById(R.id.bottom_navigation)
                        bottomNav.visibility = View.VISIBLE
                    }
                }
                Log.d("ViewModelProfile: ", userProfile.toString())
            })
    }

}