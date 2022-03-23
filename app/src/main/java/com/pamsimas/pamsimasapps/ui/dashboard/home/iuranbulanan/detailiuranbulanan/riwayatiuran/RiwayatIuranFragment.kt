package com.pamsimas.pamsimasapps.ui.dashboard.home.iuranbulanan.detailiuranbulanan.riwayatiuran

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentRiwayatIuranBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.models.Iuran
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.daftarpelanggan.DaftarPelangganAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class RiwayatIuranFragment : Fragment() {

    private lateinit var binding: FragmentRiwayatIuranBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var customerData: Customers

    private val riwayatIuranAdapter = RiwayatIuranAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRiwayatIuranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        customerData = Customers()
        mainViewModel.getCustomerData()
            .observe(viewLifecycleOwner) { customers ->
                if (customers != null) {
                    customerData = customers
                    setRvIuranCustomer(customerData)
                }
            }
    }

    private fun setRvIuranCustomer(customerData: Customers) {
        showProgressBar(true)
        mainViewModel.getListIuran(customerData.customerId.toString())
            .observe(viewLifecycleOwner) { dataIuran ->
                if (dataIuran != null && dataIuran.isNotEmpty()) {
                    showProgressBar(false)
                    riwayatIuranAdapter.setListIuran(dataIuran)
                    setIuranAdapter()
                    binding.linearEmpty.visibility = View.GONE
                    binding.rvIuran.visibility = View.VISIBLE
                } else {
                    showProgressBar(false)
                    binding.linearEmpty.visibility = View.VISIBLE
                    binding.rvIuran.visibility = View.GONE
                }
            }
    }

    private fun setIuranAdapter() {
        with(binding.rvIuran) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = riwayatIuranAdapter
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