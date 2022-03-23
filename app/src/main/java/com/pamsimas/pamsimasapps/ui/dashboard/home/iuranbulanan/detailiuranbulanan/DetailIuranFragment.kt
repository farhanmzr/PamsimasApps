package com.pamsimas.pamsimasapps.ui.dashboard.home.iuranbulanan.detailiuranbulanan

import android.content.ContentValues
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.FirebaseFirestore
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentDetailIuranBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.models.Kegiatan
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.iuranbulanan.detailiuranbulanan.bayariuran.BayarIuranFragment
import com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas.detail.DetailKegiatanPamsimasFragment
import com.pamsimas.pamsimasapps.utils.getMonthString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
class DetailIuranFragment : Fragment() {

    private lateinit var binding: FragmentDetailIuranBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var customerData: Customers

    companion object {
        const val EXTRA_ID = "extra_id"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailIuranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerData = Customers()
        if (arguments != null) {
            val customerId = requireArguments().getString(EXTRA_ID)
            mainViewModel.setCustomerData(customerId.toString())
                .observe(viewLifecycleOwner) { customers ->
                    if (customers != null) {
                        customerData = customers
                        getCustomerData()
                    }
                }
        }

        initTabLayout()

        binding.icBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStackImmediate()
        }
        binding.btnBayarIuran.setOnClickListener {
            val mBayarIuran = BayarIuranFragment()
            val mFragmentManager = parentFragmentManager
            mFragmentManager.commit {
                addToBackStack(null)
                replace(
                    R.id.host_fragment_activity_main,
                    mBayarIuran
                )
            }
        }

    }

    private fun getCustomerData() {
        mainViewModel.getCustomerData()
            .observe(viewLifecycleOwner) { customers ->
                if (customers != null) {
                    customerData = customers
                    setViewCustomerData(customerData)
                }
            }
    }


    private fun setViewCustomerData(customerData: Customers) {
        binding.apply {
            tvNamaPelanggan.text = customerData.customerName
            tvNik.text = customerData.customerNIK
            tvStatusTagihan.text = customerData.statusTagihan

            btnBayarIuran.isEnabled = customerData.statusTagihan != "Sudah Bayar"

        }
    }

    private fun initTabLayout() {
        val sectionAdapter = SectionDetailIuranAdapter(this)
        binding.viewPager.adapter = sectionAdapter
        binding.viewPager.isSaveEnabled = false
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Ringkasan"
                1 -> tab.text = "Riwayat"
            }
        }.attach()
    }

}