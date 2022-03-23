package com.pamsimas.pamsimasapps.ui.dashboard.home.iuranbulanan.detailiuranbulanan.ringkasaniuran

import android.app.Dialog
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
import com.google.firebase.firestore.FirebaseFirestore
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentRingkasanIuranBinding
import com.pamsimas.pamsimasapps.databinding.FragmentRiwayatIuranBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.models.Iuran
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.utils.PriceFormatHelper
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import com.pamsimas.pamsimasapps.utils.getMonthString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
class RingkasanIuranFragment : Fragment() {

    private lateinit var binding: FragmentRingkasanIuranBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var customerData: Customers
    private lateinit var iuranData: Iuran

    private val timeOut: Long = 1000
    private lateinit var progressDialog : Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRingkasanIuranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        progressDialog.show()

        customerData = Customers()
        mainViewModel.getCustomerData()
            .observe(viewLifecycleOwner) { customers ->
                if (customers != null) {
                    customerData = customers
                    checkedAvailableMeteran(customerData)
                }
            }
    }

    private fun checkedAvailableMeteran(customerData: Customers) {

        //getMonthYearNow
        val now = Calendar.getInstance(TimeZone.getDefault())
        val month = now.get(Calendar.MONTH)
        val year = now.get(Calendar.YEAR)
        val date = "${getMonthString(month)} $year"
        Log.e("monthYear", date)

        //Check to database if meteran available or not
        val firestoreRef: FirebaseFirestore = FirebaseFirestore.getInstance()
        //delayView
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            firestoreRef.collection("customers").document(customerData.customerId.toString()).collection("iuran")
                .whereEqualTo("dateInput", date).get().addOnSuccessListener { documentsIuran ->
                    if (!documentsIuran.isEmpty) {
                        for (document in documentsIuran) {
                            Log.d(ContentValues.TAG, "${document.id} => ${document.data}")
                            progressDialog.dismiss()
                            setRingkasanId(document.id)
                            showView(true)
                        }
                    } else {
                        progressDialog.dismiss()
                        showView(false)
                        Toast.makeText(requireContext(), "Pelanggan belum mempunyai ringkasan tagihan.", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                }
        }, timeOut)
    }

    private fun setRingkasanId(iuranId: String) {
        mainViewModel.setIuranData(customerData.customerId.toString(), iuranId)
            .observe(viewLifecycleOwner) { iuran ->
                if (iuran != null) {
                    iuranData = iuran
                    getIuranData()
                }
            }
    }

    private fun getIuranData() {
        mainViewModel.getIuranData()
            .observe(viewLifecycleOwner) { iuran ->
                if (iuran != null) {
                    iuranData = iuran
                    setViewIuranData(iuranData)
                }
            }
    }

    private fun setViewIuranData(iuranData: Iuran) {
        binding.apply {
            tvStandAwal.text = iuranData.standAwal + " m³"
            tvStandAkhir.text = iuranData.standAkhir + " m³"
            tvPemakaian.text = iuranData.pemakaian
            val biayaAir = iuranData.biayaAir?.toInt()
            val pipa = iuranData.pipa?.toInt()
            val totalBiaya = iuranData.totalBiaya?.toInt()
            tvBiayaAir.text = PriceFormatHelper.getPriceFormat(biayaAir!!)
            tvPipa.text = PriceFormatHelper.getPriceFormat(pipa!!)
            tvTotalBiaya.text = PriceFormatHelper.getPriceFormat(totalBiaya!!)


        }
    }

    private fun showView(state: Boolean) {
        if (state) {
            binding.linearData.visibility = View.VISIBLE
            binding.linearEmpty.visibility = View.GONE
        } else {
            binding.linearData.visibility = View.GONE
            binding.linearEmpty.visibility = View.VISIBLE
        }
    }

}