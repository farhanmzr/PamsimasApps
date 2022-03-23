package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.detailpelanggan

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentDetailPelangganBinding
import com.pamsimas.pamsimasapps.databinding.ItemDialogHapusPelangganBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.ui.dashboard.MainActivity
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.PelangganViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.detailpelanggan.inputmeteran.InputMeteranOneFragment
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.editpelanggan.EditPelangganFragment
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.tambahpelanggan.TambahPelangganFragment
import com.pamsimas.pamsimasapps.utils.AppConstants.STATUS_SUCCESS
import com.pamsimas.pamsimasapps.utils.AppConstants.SUDAH_DIINPUT
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DetailPelangganFragment : Fragment() {

    private lateinit var binding : FragmentDetailPelangganBinding
    private val pelangganViewModel: PelangganViewModel by activityViewModels()

    private val detailPelangganAdapter = DetailPelangganAdapter()
    private lateinit var customerData: Customers
    private lateinit var progressDialog : Dialog

    companion object {
        const val EXTRA_PELANGGAN_DATA = "extra_pelanggan_data"
        const val EXTRA_FROM_HOME = "extra_from_home"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailPelangganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())

        customerData = Customers()
        if (arguments != null) {
            customerData = requireArguments().getParcelable(EXTRA_PELANGGAN_DATA)!!
            pelangganViewModel.setCustomerData(customerData.customerId.toString())
                .observe(viewLifecycleOwner) { customers ->
                    if (customers != null) {
                        customerData = customers
                        Log.e("data", customerData.customerName.toString())
                    }
                }
        }

        getCustomerData()

        binding.apply {

            icBack.setOnClickListener {
                if (requireArguments().getBoolean(EXTRA_FROM_HOME)){
                    activity?.onBackPressed()
                } else {
                    requireActivity().supportFragmentManager.popBackStackImmediate()
                }
            }

            btnInputMeteran.setOnClickListener {
                setCurrentFragment(InputMeteranOneFragment())
            }

            toolbar.inflateMenu(R.menu.menu_option)
            toolbar.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.edit -> setCurrentFragment(EditPelangganFragment())
                    R.id.hapus -> openDialogHapusPelanggan()
                }
                true
            }
        }
    }

    private fun openDialogHapusPelanggan() {
        val builder = AlertDialog.Builder(requireContext())
        val binding: ItemDialogHapusPelangganBinding =
            ItemDialogHapusPelangganBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.show()
        binding.apply {

            val name = customerData.customerName
            tvDetail.text = "Apakah anda yakin untuk memberhentikan langganan atas nama $name"

            btnBatalkan.setOnClickListener { dialog.dismiss() }
            btnKonfirmasi.setOnClickListener { hapusPelanggan()
                dialog.dismiss() }
        }
    }

    private fun hapusPelanggan() {
        progressDialog.show()
        pelangganViewModel.deleteCustomerData(customerData.customerId.toString())
            .observe(viewLifecycleOwner) { status ->
                if (status == STATUS_SUCCESS) {
                    progressDialog.dismiss()
                    requireActivity().supportFragmentManager.popBackStackImmediate()
                    Toast.makeText(requireContext(), "Pelanggan ${customerData.customerName} berhasil berhenti berlangganan.", Toast.LENGTH_SHORT).show()
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun setDataRiwayatPemakaianRv(customerData: Customers) {
        showProgressBar(true)
        pelangganViewModel.getRiwayatPemakaian(customerData.customerId.toString())
            .observe(viewLifecycleOwner) { dataPemakaian ->
                if (dataPemakaian != null && dataPemakaian.isNotEmpty()) {
                    showProgressBar(false)
                    showView(true)
                    detailPelangganAdapter.setListMeteran(dataPemakaian)
                    setRvAdapter()
                } else {

                    showProgressBar(false)
                    showView(false)
                }
            }
    }

    private fun setRvAdapter() {
        with(binding.rvRiwayatPemakaian) {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            adapter = detailPelangganAdapter
        }
    }

    private fun showView(state: Boolean) {
        if (state) {
            binding.linearEmpty.visibility = View.GONE
            binding.rvRiwayatPemakaian.visibility = View.VISIBLE
            binding.tableLayout.visibility = View.VISIBLE
        } else {
            binding.rvRiwayatPemakaian.visibility = View.GONE
            binding.linearEmpty.visibility = View.VISIBLE
            binding.tableLayout.visibility = View.GONE
        }
    }

    private fun showProgressBar(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun getCustomerData() {
        pelangganViewModel.getCustomerData()
            .observe(viewLifecycleOwner) { customers ->
                if (customers != null) {
                    customerData = customers
                    setViewCustomerData(customerData)
                    setDataRiwayatPemakaianRv(customerData)
                }
            }
    }

    private fun setViewCustomerData(customerData: Customers) {
        with(binding){
            tvNamaLengkap.text = customerData.customerName
            tvAlamat.text = customerData.customerAlamat
            tvGender.text = customerData.customerGender
            tvKota.text = customerData.customerKotaKab
            tvKodepos.text = customerData.customerKodePos
            tvNik.text = customerData.customerNIK
            tvPhone.text = customerData.customerPhone
            tvProvinsi.text = customerData.customerProvinsi

            btnInputMeteran.isEnabled = customerData.statusInput != SUDAH_DIINPUT
        }
    }

    private fun setCurrentFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.host_fragment_activity_pelanggan, fragment, MainActivity.CHILD_FRAGMENT)
        }
    }

}