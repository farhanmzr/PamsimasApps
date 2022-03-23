package com.pamsimas.pamsimasapps.ui.dashboard.home.iuranbulanan.detailiuranbulanan.bayariuran

import android.app.Dialog
import android.content.ContentValues
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.pamsimas.pamsimasapps.databinding.FragmentBayarIuranBinding
import com.pamsimas.pamsimasapps.databinding.ItemDialogKonfirmasiBayarIuranBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.models.Iuran
import com.pamsimas.pamsimasapps.models.Laporan
import com.pamsimas.pamsimasapps.models.RingkasanLaporan
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas.KegiatanPamsimasFragment
import com.pamsimas.pamsimasapps.utils.AppConstants
import com.pamsimas.pamsimasapps.utils.AppConstants.SUDAH_BAYAR
import com.pamsimas.pamsimasapps.utils.PriceFormatHelper
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import com.pamsimas.pamsimasapps.utils.getMonthString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.DecimalFormat
import java.util.*

@ExperimentalCoroutinesApi
class BayarIuranFragment : Fragment() {

    private lateinit var binding: FragmentBayarIuranBinding

    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var iuranData: Iuran
    private lateinit var customerData: Customers
    private lateinit var laporanData: Laporan

    private lateinit var progressDialog : Dialog

    private var totalBiaya: Int? = null
    private var jumlahInput: Int? = null
    //Laporan
    private var laporanId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBayarIuranBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        getIuranData()

    }

    private fun getIuranData() {
        mainViewModel.getIuranData()
            .observe(viewLifecycleOwner) { iuran ->
                if (iuran != null) {
                    iuranData = iuran
                    initView(iuranData)
                }
            }
    }

    private fun initView(iuranData: Iuran) {
        binding.apply {

            totalBiaya = iuranData.totalBiaya?.toInt()
            tvTotalBiaya.text = totalBiaya?.let { PriceFormatHelper.getPriceFormat(it) }

            icBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
            btnBayarTagihan.isEnabled = false

            fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
                this.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        btnBayarTagihan.isEnabled = false
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        btnBayarTagihan.isEnabled = false
                        var input: String = s.toString()

                        if (input.isNotEmpty()) {
                            input = input.replace(",", "")
                            val format = DecimalFormat("#,###,###")
                            val newPrice: String = format.format(input.toDouble())
                            etJumlahBayar.removeTextChangedListener(this) //To Prevent from Infinite Loop
                            etJumlahBayar.setText(newPrice)
                            etJumlahBayar.setSelection(newPrice.length) //Move Cursor to end of String
                            etJumlahBayar.addTextChangedListener(this)
                        }
                    }

                    override fun afterTextChanged(editable: Editable?) {
                        btnBayarTagihan.isEnabled = true
                        afterTextChanged.invoke(editable.toString())
                    }
                })
            }

            etJumlahBayar.afterTextChanged { searchQuery ->
                btnBayarTagihan.isEnabled = true
                btnBayarTagihan.setOnClickListener {
                    jumlahInput = searchQuery.replace(",", "").toInt()
                    totalBiaya = iuranData.totalBiaya?.toInt()
                    Log.e("totalBiaya", totalBiaya.toString())
                    if (jumlahInput!! > totalBiaya!! || jumlahInput == totalBiaya) {
                        openDialogKonfirmasi()
                    } else {
                        etJumlahBayar.text?.clear()
                        Toast.makeText(requireContext(), "Jumlah yang harus dibayarkan kurang. Mohon masukkan jumlah dengan benar.", Toast.LENGTH_LONG).show()
                    }
                }
            }

        }
    }

    private fun openDialogKonfirmasi() {
        val builder = AlertDialog.Builder(requireContext())
        val binding: ItemDialogKonfirmasiBayarIuranBinding =
            ItemDialogKonfirmasiBayarIuranBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.show()
        binding.apply {

            tvJumlahDibayarkan.text = PriceFormatHelper.getPriceFormat(jumlahInput!!)
            tvTotalTagihan.text = PriceFormatHelper.getPriceFormat(totalBiaya!!)
            val kembalian = jumlahInput!! - totalBiaya!!
            tvKembalian.text = PriceFormatHelper.getPriceFormat(kembalian)

            btnBatalkan.setOnClickListener { dialog.dismiss() }
            btnKonfirmasi.setOnClickListener { dialog.dismiss()
                bayarTagihan() }
        }
    }

    private fun bayarTagihan() {
        progressDialog.show()
        iuranData.statusIuran = SUDAH_BAYAR
        mainViewModel.updateIuranData(iuranData).observe(viewLifecycleOwner) { updateOrder ->
            if (updateOrder != null) {
                updateStatusCustomer()
            } else {
                progressDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    "Update Status Iuran Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun updateStatusCustomer() {
        mainViewModel.getCustomerData()
            .observe(viewLifecycleOwner) { customers ->
                if (customers != null) {
                    customerData = customers
                    val status = SUDAH_BAYAR
                    customerData.statusTagihan = status
                    mainViewModel.updateCustomerData(customerData)
                        .observe(viewLifecycleOwner) { updateOrder ->
                            if (updateOrder != null) {
                                setLaporanKeuangan()
                            } else {
                                progressDialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "Update Order Failed",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }
    }

    private fun setLaporanKeuangan() {
        //getMonthYearNow
        val now = Calendar.getInstance(TimeZone.getDefault())
        val month = now.get(Calendar.MONTH)
        val year = now.get(Calendar.YEAR)
        val date = "${getMonthString(month)} $year"
        val dateNotSpace = "${getMonthString(month)}$year"
        laporanId = "laporan$dateNotSpace"

        //Check to database
        val firestoreRef: FirebaseFirestore = FirebaseFirestore.getInstance()
        val laporanRef: CollectionReference = firestoreRef.collection("laporanKeuangan")
        laporanRef.document(laporanId!!).get().addOnSuccessListener { documents ->
            if (documents.exists()){
                Toast.makeText(requireContext(), "ada", Toast.LENGTH_SHORT).show()
                mainViewModel.setLaporanData(laporanId!!)
                    .observe(viewLifecycleOwner) { document ->
                        if (document != null) {
                            laporanData = document
                            mainViewModel.getLaporanData()
                                .observe(viewLifecycleOwner) { laporan ->
                                    if (laporan != null) {
                                        laporanData = laporan
                                        val lastPemasukan = laporanData.laporanPemasukan
                                        val updatePemasukan = lastPemasukan?.plus(totalBiaya!!)
                                        laporanData.laporanPemasukan = updatePemasukan
                                        updateLaporanData(laporanData)
                                    }
                                }
                        } else {
                            Toast.makeText(requireContext(), "kosong", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                val laporanInfo = Laporan(
                    laporanId = laporanId,
                    laporanDate = "Periode Bulan $date",
                    laporanPemasukan = totalBiaya,
                    laporanPengeluaran = 0
                )
                mainViewModel.uploadLaporanData(laporanInfo).observe(viewLifecycleOwner) { status ->
                    if (status == AppConstants.STATUS_SUCCESS) {
                        uploadRingkasanLaporan()
                    } else {
                        Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
                    }
                }
                Toast.makeText(requireContext(), "kosonggg", Toast.LENGTH_SHORT).show()
            }
        }
            .addOnFailureListener { exception ->
                Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                progressDialog.dismiss()
            }

    }

    private fun updateLaporanData(laporanData: Laporan) {
        mainViewModel.updateLaporanData(laporanData).observe(viewLifecycleOwner) { updateLaporan ->
            if (updateLaporan != null) {
                uploadRingkasanLaporan()
            } else {
                progressDialog.dismiss()
                Toast.makeText(
                    requireActivity(),
                    "Update Status Iuran Failed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun uploadRingkasanLaporan() {
        val ringkasanInfo = RingkasanLaporan(
            ringkasanKategori = "Iuran Bulanan",
            ringkasanPrice = totalBiaya,
            ringkasanTipe = "Pemasukan",
            ringkasanKeterangan = "a.n. ${customerData.customerName}"
        )
        mainViewModel.uploadRingkasanData(laporanId.toString(),ringkasanInfo).observe(viewLifecycleOwner) { status ->
            if (status == AppConstants.STATUS_SUCCESS) {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), "Berhasil membayar iuran bulanan pelanggan.", Toast.LENGTH_LONG).show()
                requireActivity().supportFragmentManager.popBackStackImmediate()
            } else {
                Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
            }
        }
    }

}