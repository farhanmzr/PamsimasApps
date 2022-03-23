package com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas.detail

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentDetailKegiatanPamsimasBinding
import com.pamsimas.pamsimasapps.models.Kegiatan
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.utils.loadImage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DetailKegiatanPamsimasFragment : Fragment() {

    private lateinit var binding : FragmentDetailKegiatanPamsimasBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var kegiatanData: Kegiatan

    companion object {
        const val EXTRA_KEGIATAN_DATA = "extra_kegiatan_data"
        const val FROM_HOME = "from_home"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailKegiatanPamsimasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        kegiatanData = Kegiatan()
        if (arguments != null) {
            kegiatanData = requireArguments().getParcelable(EXTRA_KEGIATAN_DATA)!!
            mainViewModel.setKegiatanData(kegiatanData.kegiatanId.toString())
                .observe(viewLifecycleOwner) { kegiatan ->
                    if (kegiatan != null) {
                        kegiatanData = kegiatan
                    }
                }
        }

        val bottomNav: BottomNavigationView =
            requireActivity().findViewById(R.id.bottom_navigation)

        if (requireArguments().getBoolean(FROM_HOME)) {
            bottomNav.visibility = View.GONE
        }

        binding.icBack.setOnClickListener {
            if (requireArguments().getBoolean(FROM_HOME)){
                bottomNav.visibility = View.VISIBLE
                requireActivity().supportFragmentManager.popBackStackImmediate()
            } else {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
        }

        getKegiatanData()

    }

    private fun getKegiatanData() {
        mainViewModel.getKegiatanData()
            .observe(viewLifecycleOwner) { kegiatan ->
                if (kegiatan != null) {
                    kegiatanData = kegiatan
                    setViewKegiatanData(kegiatanData)
                }
            }
    }

    private fun setViewKegiatanData(kegiatanData: Kegiatan) {
        with(binding){
            imgKegiatan.loadImage(kegiatanData.kegiatanPicture)
            tvJudulKegiatan.text = kegiatanData.kegiatanName
            tvDate.text = kegiatanData.kegiatanDate
            tvDesc.text = kegiatanData.kegiatanDesc
        }
    }

}