package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.daftarpelanggan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.pamsimas.pamsimasapps.databinding.FragmentContentDaftarPelangganBinding
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.PelangganViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.daftarpelanggan.dialogfilterpelanggan.DialogFilterPelangganFragment
import com.pamsimas.pamsimasapps.utils.AppConstants.BELUM_DIINPUT
import com.pamsimas.pamsimasapps.utils.AppConstants.SUDAH_DIINPUT
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ContentDaftarPelangganFragment : Fragment() {

    private lateinit var binding: FragmentContentDaftarPelangganBinding

    private val pelangganViewModel: PelangganViewModel by activityViewModels()

    private val daftarPelangganAdapter = DaftarPelangganAdapter()

    private var sort: Boolean = false
    private var aToz: String? = null
    private var zToA: String? = null
    private var sudahBayar: String? = null
    private var belumBayar: String? = null

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(index: Int) =
            ContentDaftarPelangganFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, index)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentContentDaftarPelangganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFilter.setOnClickListener {
            sort = false
            aToz = null
            zToA = null
            sudahBayar = null
            belumBayar = null
            val mDialogFilterPelanggan = DialogFilterPelangganFragment()
            val mFragmentManager = childFragmentManager
            mDialogFilterPelanggan.show(
                mFragmentManager,
                mDialogFilterPelanggan::class.java.simpleName
            )
        }

        when (arguments?.getInt(ARG_SECTION_NUMBER, 0)) {
            1 -> {
                getCustomers(
                    BELUM_DIINPUT,
                    sort,
                    aToz.toString(),
                    zToA.toString(),
                    sudahBayar.toString(),
                    belumBayar.toString()
                )
            }
            2 -> {
                getCustomers(
                    SUDAH_DIINPUT,
                    sort,
                    aToz.toString(),
                    zToA.toString(),
                    sudahBayar.toString(),
                    belumBayar.toString()
                )
            }
        }
    }

    private fun getCustomers(
        status: String,
        sort: Boolean,
        aToz: String,
        zToA: String,
        sudahBayar: String,
        belumBayar: String
    ) {
        showProgressBar(true)
        pelangganViewModel.getListCustomers(status, sort, aToz, zToA, sudahBayar, belumBayar)
            .observe(viewLifecycleOwner) { customersStatus ->
                when (status) {
                    BELUM_DIINPUT -> {
                        if (customersStatus != null && customersStatus.isNotEmpty()) {
                            showProgressBar(false)
                            showView(true)
                            daftarPelangganAdapter.setListCustomers(customersStatus)
                            setDaftarPelangganAdapter()
                        } else {
                            showView(false)
                            showProgressBar(false)
                        }
                    }
                    SUDAH_DIINPUT -> {
                        if (customersStatus != null && customersStatus.isNotEmpty()) {
                            showProgressBar(false)
                            showView(true)
                            daftarPelangganAdapter.setListCustomers(customersStatus)
                            setDaftarPelangganAdapter()
                        } else {
                            showView(false)
                            showProgressBar(false)
                        }
                    }
                }
            }
    }

    private fun setDaftarPelangganAdapter() {
        with(binding.rvListPelanggan) {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = daftarPelangganAdapter
        }
    }

    private fun showView(state: Boolean) {
        if (state) {
            binding.apply {
                rvListPelanggan.visibility = View.VISIBLE
                btnFilter.visibility = View.VISIBLE
                linearEmpty.visibility = View.GONE
            }
        } else {
            binding.apply {
                rvListPelanggan.visibility = View.GONE
                btnFilter.visibility = View.GONE
                linearEmpty.visibility = View.VISIBLE
            }
        }
    }

    internal var optionDialogListener: DialogFilterPelangganFragment.OnOptionDialogListener =
        object : DialogFilterPelangganFragment.OnOptionDialogListener {
            override fun onOptionChosen(filter: String?) {
                when (filter) {
                    "A sampai Z" -> {
                        aToz = filter
                    }
                    "Z sampai A" -> {
                        zToA = filter
                    }
                    "Tagihan Sudah Dibayar" -> {
                        sudahBayar = "Sudah Dibayar"
                    }
                    "Tagihan Belum Dibayar" -> {
                        belumBayar = "Belum Dibayar"
                    }
                }
                sort = true
                when (arguments?.getInt(ARG_SECTION_NUMBER, 0)) {
                    1 -> {
                        getCustomers(
                            BELUM_DIINPUT,
                            sort,
                            aToz.toString(),
                            zToA.toString(),
                            sudahBayar.toString(),
                            belumBayar.toString()
                        )
                    }
                    2 -> {
                        getCustomers(
                            SUDAH_DIINPUT,
                            sort,
                            aToz.toString(),
                            zToA.toString(),
                            sudahBayar.toString(),
                            belumBayar.toString()
                        )
                    }
                }

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