package com.pamsimas.pamsimasapps.ui.dashboard.home.tentangpamsimas

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentTentangPamsimasBinding


class TentangPamsimasFragment : Fragment() {

    private lateinit var binding: FragmentTentangPamsimasBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTentangPamsimasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initTabLayout()
    }

    private fun initView() {
        binding.apply {
            val bottomNav: BottomNavigationView =
                requireActivity().findViewById(R.id.bottom_navigation)
            bottomNav.visibility = View.GONE
            icBack.setOnClickListener {
                bottomNav.visibility = View.VISIBLE
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
        }
    }

    private fun initTabLayout() {
        val sectionAdapter = SectionTentangPamsimasAdapter(this)
        binding.viewPager.adapter = sectionAdapter
        binding.viewPager.isSaveEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Profil"
                1 -> tab.text = "Latar Belakang"
                2 -> tab.text = "Aset"
            }
        }.attach()
    }

}