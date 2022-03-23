package com.pamsimas.pamsimasapps.ui.dashboard.home.tentangpamsimas.aset

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentAsetPamsimasBinding
import com.pamsimas.pamsimasapps.databinding.FragmentProfilePamsimasBinding


class AsetPamsimasFragment : Fragment() {

    private lateinit var binding: FragmentAsetPamsimasBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAsetPamsimasBinding.inflate(inflater, container, false)
        return binding.root
    }

}