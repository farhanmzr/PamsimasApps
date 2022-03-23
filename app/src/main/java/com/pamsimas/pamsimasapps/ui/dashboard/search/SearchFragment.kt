package com.pamsimas.pamsimasapps.ui.dashboard.search

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.pamsimas.pamsimasapps.databinding.FragmentSearchBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.PelangganActivity
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.daftarpelanggan.DaftarPelangganAdapter
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class SearchFragment : Fragment(), SearchClickCallback {

    private lateinit var binding : FragmentSearchBinding

    private lateinit var progressDialog : Dialog
    private val mainViewModel: MainViewModel by activityViewModels()
    private val searchAdapter = SearchAdapter(this@SearchFragment)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())

        binding.etSearch.setOnEditorActionListener(TextView.OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val searchQuery = binding.etSearch.text.toString()
                showResult(searchQuery)
                return@OnEditorActionListener true
            }
            false
        })

    }

    private fun showResult(searchQuery: String) {
        progressDialog.show()
        mainViewModel.getListSearch(searchQuery)
            .observe(viewLifecycleOwner) { customersStatus ->
                if (customersStatus != null && customersStatus.isNotEmpty()) {
                    progressDialog.dismiss()
                    searchAdapter.setListSearch(customersStatus)
                    setAdapter()
                    binding.linearEmpty.visibility = View.GONE
                    binding.rvListOrderTransaction.visibility = View.VISIBLE
                } else {
                    progressDialog.dismiss()
                    binding.rvListOrderTransaction.visibility = View.GONE
                    binding.linearEmpty.visibility = View.VISIBLE
                }
            }
    }

    private fun setAdapter() {
        with(binding.rvListOrderTransaction) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = searchAdapter
        }
    }

    override fun onItemClicked(data: Customers) {
        val intent =
            Intent(requireActivity(), PelangganActivity::class.java)
        intent.putExtra(PelangganActivity.EXTRA_DATA, data)
        intent.putExtra(PelangganActivity.EXTRA_HOME, true)
        startActivity(intent)
    }

}

