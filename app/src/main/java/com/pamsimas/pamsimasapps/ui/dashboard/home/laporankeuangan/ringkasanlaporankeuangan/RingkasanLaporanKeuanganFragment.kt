package com.pamsimas.pamsimasapps.ui.dashboard.home.laporankeuangan.ringkasanlaporankeuangan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentLaporanKeuanganBinding
import com.pamsimas.pamsimasapps.databinding.FragmentRingkasanLaporanKeuanganBinding
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.laporankeuangan.LaporanKeuanganAdapter
import com.pamsimas.pamsimasapps.ui.dashboard.home.laporankeuangan.tambahlaporankeuangan.TambahLaporanKeuanganFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class RingkasanLaporanKeuanganFragment : Fragment() {

    private lateinit var binding: FragmentRingkasanLaporanKeuanganBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private val ringkasanLaporanAdapter = RingkasanLaporanKeuanganAdapter()

    private var laporanId: String? = null
    private var laporanDate: String? = null

    companion object {
        const val EXTRA_LAPORAN_ID = "extra_laporan_id"
        const val EXTRA_LAPORAN_DATE = "extra_laporan_date"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRingkasanLaporanKeuanganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments!=null){
            laporanId = requireArguments().getString(EXTRA_LAPORAN_ID)
            laporanDate = requireArguments().getString(EXTRA_LAPORAN_DATE)
        }

        binding.apply {

            icBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
            tvPeriode.text = laporanDate
        }

        setDataRingkasanLaporanRv()
    }

    private fun setDataRingkasanLaporanRv() {
        showProgressBar(true)
        mainViewModel.getListRingkasanLaporanData(laporanId.toString())
            .observe(viewLifecycleOwner) { dataRingkasan ->
                if (dataRingkasan != null && dataRingkasan.isNotEmpty()) {
                    showProgressBar(false)
                    ringkasanLaporanAdapter.setListRingkasanLaporan(dataRingkasan)
                    setKegiatanAdapter()
                } else {
                    showProgressBar(false)
                }
            }
    }

    private fun setKegiatanAdapter() {
        with(binding.rvRingkasanKeuangan) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = ringkasanLaporanAdapter
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