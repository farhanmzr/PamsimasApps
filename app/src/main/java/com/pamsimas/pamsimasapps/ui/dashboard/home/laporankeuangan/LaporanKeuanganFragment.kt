package com.pamsimas.pamsimasapps.ui.dashboard.home.laporankeuangan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentLaporanKeuanganBinding
import com.pamsimas.pamsimasapps.ui.dashboard.MainActivity
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.laporankeuangan.tambahlaporankeuangan.TambahLaporanKeuanganFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class LaporanKeuanganFragment : Fragment() {

    private lateinit var binding: FragmentLaporanKeuanganBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private val laporanAdapter = LaporanKeuanganAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLaporanKeuanganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomNav: BottomNavigationView =
            requireActivity().findViewById(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE

        binding.apply {

            icBack.setOnClickListener {
                bottomNav.visibility = View.VISIBLE
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
            btnTambahLaporan.setOnClickListener {
                setCurrentFragment(TambahLaporanKeuanganFragment())
            }
        }

        setDataLaporanRv()
    }

    private fun setCurrentFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.host_fragment_activity_main, fragment)
        }
    }

    private fun setDataLaporanRv() {
        showProgressBar(true)
        mainViewModel.getListLaporanData()
            .observe(viewLifecycleOwner) { dataLaporan ->
                if (dataLaporan != null && dataLaporan.isNotEmpty()) {
                    showProgressBar(false)
                    laporanAdapter.setListLaporan(dataLaporan)
                    setKegiatanAdapter()
                } else {
                    showProgressBar(false)
                }
            }
    }

    private fun setKegiatanAdapter() {
        with(binding.rvLaporanKeuangan) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = laporanAdapter
        }
    }

    private fun showProgressBar(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}