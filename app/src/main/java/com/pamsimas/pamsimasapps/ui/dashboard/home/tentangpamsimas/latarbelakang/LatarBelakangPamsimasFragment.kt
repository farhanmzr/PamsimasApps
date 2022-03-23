package com.pamsimas.pamsimasapps.ui.dashboard.home.tentangpamsimas.latarbelakang

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentLatarBelakangPamsimasBinding
import com.pamsimas.pamsimasapps.databinding.FragmentProfilePamsimasBinding


class LatarBelakangPamsimasFragment : Fragment() {

    private lateinit var binding: FragmentLatarBelakangPamsimasBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLatarBelakangPamsimasBinding.inflate(inflater, container, false)
        return binding.root
    }


}