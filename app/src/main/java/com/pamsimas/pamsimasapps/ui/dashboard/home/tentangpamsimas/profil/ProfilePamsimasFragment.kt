package com.pamsimas.pamsimasapps.ui.dashboard.home.tentangpamsimas.profil

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentProfilePamsimasBinding


class ProfilePamsimasFragment : Fragment() {

    private lateinit var binding: FragmentProfilePamsimasBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfilePamsimasBinding.inflate(inflater, container, false)
        return binding.root
    }

}