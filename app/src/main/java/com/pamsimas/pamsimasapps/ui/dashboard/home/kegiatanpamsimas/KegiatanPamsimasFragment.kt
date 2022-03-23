package com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas

import android.app.Dialog
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
import com.pamsimas.pamsimasapps.databinding.FragmentKegiatanPamsimasBinding
import com.pamsimas.pamsimasapps.databinding.FragmentLoginBinding
import com.pamsimas.pamsimasapps.models.Kegiatan
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas.detail.DetailKegiatanPamsimasFragment
import com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas.tambahkegiatan.TambahKegiatanPamsimasFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class KegiatanPamsimasFragment : Fragment(), KegiatanPamsimasClickCallback {

    private lateinit var binding : FragmentKegiatanPamsimasBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private val kegiatanAdapter = KegiatanPamsimasAdapter(this@KegiatanPamsimasFragment)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentKegiatanPamsimasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        setDataKegiatanRv()
    }

    private fun initView() {
        with(binding){
            val bottomNav: BottomNavigationView =
                requireActivity().findViewById(R.id.bottom_navigation)
            bottomNav.visibility = View.GONE

            icBack.setOnClickListener {
                bottomNav.visibility = View.VISIBLE
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }

            btnTambahKegiatan.setOnClickListener {
                val mTambahKegiatan = TambahKegiatanPamsimasFragment()
                val mFragmentManager = parentFragmentManager
                mFragmentManager.commit {
                    addToBackStack(null)
                    replace(
                        R.id.host_fragment_activity_main,
                        mTambahKegiatan
                    )
                }
            }
        }
    }

    private fun setDataKegiatanRv() {
        showProgressBar(true)
        mainViewModel.getListKegiatan()
            .observe(viewLifecycleOwner) { dataKegiatan ->
                if (dataKegiatan != null && dataKegiatan.isNotEmpty()) {
                    showProgressBar(false)
                    kegiatanAdapter.setListKegiatan(dataKegiatan)
                    setKegiatanAdapter()
                } else {
                    showProgressBar(false)
                }
            }
    }

    private fun setKegiatanAdapter() {
        with(binding.rvKegiatanPamsimas) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = kegiatanAdapter
        }
    }

    private fun showProgressBar(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onItemClicked(data: Kegiatan) {
        val mDetailKegiatanPamsimas = DetailKegiatanPamsimasFragment()
        val mBundle = Bundle()
        mBundle.putParcelable(DetailKegiatanPamsimasFragment.EXTRA_KEGIATAN_DATA, data)
        mDetailKegiatanPamsimas.arguments = mBundle

        val mFragmentManager = parentFragmentManager
        mFragmentManager.commit {
            addToBackStack(null)
            replace(
                R.id.host_fragment_activity_main,
                mDetailKegiatanPamsimas
            )
        }
    }

    override fun onResume() {
        super.onResume()
        setDataKegiatanRv()
    }


}