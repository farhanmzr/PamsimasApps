package com.pamsimas.pamsimasapps.ui.dashboard.home.laporankeuangan.tambahlaporankeuangan

import android.app.Dialog
import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentLaporanKeuanganBinding
import com.pamsimas.pamsimasapps.databinding.FragmentTambahLaporanKeuanganBinding
import com.pamsimas.pamsimasapps.models.Laporan
import com.pamsimas.pamsimasapps.models.RingkasanLaporan
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.utils.*
import com.pamsimas.pamsimasapps.utils.AppConstants.PEMASUKAN
import com.whiteelephant.monthpicker.MonthPickerDialog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.text.DecimalFormat
import java.util.*

@ExperimentalCoroutinesApi
class TambahLaporanKeuanganFragment : Fragment() {

    private lateinit var binding: FragmentTambahLaporanKeuanganBinding

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var laporanData: Laporan

    private lateinit var progressDialog : Dialog

    private var jumlahInput: Int? = null
    private var updatePemasukan: Int? = null
    private var updatePengeluaran: Int? = null

    private var date: String? = null
    private var dateNotSpace: String? = null
    private var jenisTransaksi: String? = null
    private var laporanId: String? = null

    private var pemasukan = 0
    private var pengeluaran = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTambahLaporanKeuanganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())

        binding.apply {

            icBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }

            rgJenisTransaksi.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rb_pemasukan -> jenisTransaksi = binding.rbPemasukan.text.toString()
                    R.id.rb_pengeluaran -> jenisTransaksi = binding.rbPengeluaran.text.toString()
                }
            }

            //DATE MONTH AND YEAR ONLY
            val today = Calendar.getInstance(TimeZone.getDefault())
            val builder = MonthPickerDialog.Builder(
                requireContext(), { selectedMonth, selectedYear ->
                    date = "${getMonthString(selectedMonth)} $selectedYear"
                    dateNotSpace = "${getMonthString(selectedMonth)}$selectedYear"
                    //setLaporanId
                    laporanId = "laporan$dateNotSpace"
                    binding.etDateLaporan.setText(date)
                },
                today.get(Calendar.YEAR),
                today.get(Calendar.MONTH)
            )
            builder.setActivatedMonth(today.get(Calendar.MONTH))
                .setMinYear(1999)
                .setActivatedYear(2022)
                .setMaxYear(2030)
                .setMinMonth(Calendar.MARCH)
                .setTitle("Pilih Bulan dan Tahun")
                .setMonthRange(Calendar.JANUARY, Calendar.DECEMBER)
            binding.etDateLaporan.setOnClickListener {
                builder.build().show()
            }

            fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
                this.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        var input: String = s.toString()
                        if (input.isNotEmpty()) {
                            input = input.replace(",", "")
                            val formatDouble = DecimalFormat("#,###,###,###,###")
                            val newPrice: String = formatDouble.format(input.toDouble())
                            etNominal.removeTextChangedListener(this) //To Prevent from Infinite Loop
                            etNominal.setText(newPrice)
                            etNominal.setSelection(newPrice.length) //Move Cursor to end of String
                            etNominal.addTextChangedListener(this)
                        }
                    }

                    override fun afterTextChanged(editable: Editable?) {
                        afterTextChanged.invoke(editable.toString())
                    }
                })
            }

            etNominal.afterTextChanged { searchQuery ->
                jumlahInput = searchQuery.replace(",", "").toInt()
                Log.e("jumlahInput", jumlahInput.toString())
            }

            btnTambahLaporan.setOnClickListener {
                if (validateInput()){
                    submitTambahLaporan()
                }
            }
        }

    }

    private fun submitTambahLaporan() {
        progressDialog.show()
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
                                        val lastPengeluaran = laporanData.laporanPengeluaran
                                        if (jenisTransaksi == PEMASUKAN) {
                                            updatePemasukan = lastPemasukan?.plus(jumlahInput!!)
                                            laporanData.laporanPemasukan = updatePemasukan
                                        } else {
                                            updatePengeluaran = lastPengeluaran?.plus(jumlahInput!!)
                                            laporanData.laporanPengeluaran = updatePengeluaran
                                        }
                                        updateLaporanData(laporanData)
                                    }
                                }
                        } else {
                            Toast.makeText(requireContext(), "kosong", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                //choice pemasukan/pengeluaran
                if (jenisTransaksi == PEMASUKAN) {
                    pemasukan = jumlahInput!!
                } else {
                    pengeluaran = jumlahInput!!
                }
                val laporanInfo = Laporan(
                    laporanId = laporanId,
                    laporanDate = "Periode Bulan $date",
                    laporanPemasukan = pemasukan,
                    laporanPengeluaran = pengeluaran
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

        val category = binding.etKategori.text.toString().trim()
        val keterangan = binding.etKeterangan.text.toString().trim()

        val ringkasanInfo = RingkasanLaporan(
            ringkasanKategori = category,
            ringkasanPrice = jumlahInput,
            ringkasanTipe = jenisTransaksi,
            ringkasanKeterangan = keterangan
        )
        mainViewModel.uploadRingkasanData(laporanId.toString(),ringkasanInfo).observe(viewLifecycleOwner) { status ->
            if (status == AppConstants.STATUS_SUCCESS) {
                progressDialog.dismiss()
                Toast.makeText(requireActivity(), "Berhasil menambahkan laporan keuangan.", Toast.LENGTH_LONG).show()
                requireActivity().supportFragmentManager.popBackStackImmediate()
            } else {
                Toast.makeText(requireContext(), status, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun validateInput(): Boolean {

        val date = binding.etDateLaporan.text.toString().trim()
        val category = binding.etKategori.text.toString().trim()
        val keterangan = binding.etKeterangan.text.toString().trim()

        return when {
            date.isEmpty() -> {
                binding.etDateLaporan.error = "Tanggal Laporan tidak boleh kosong."
                false
            }
            jumlahInput.toString().isEmpty() -> {
                Toast.makeText(requireContext(), "Nominal tidak boleh kosong.", Toast.LENGTH_SHORT).show()
                false
            }
            category.length < 5 -> {
                binding.etKategori.error = "Minimal Kategori Laporan adalah 5 huruf."
                false
            }
            keterangan.length < 10 -> {
                binding.etKategori.error = "Minimal Keterangan Laporan adalah 10 huruf."
                false
            }
            jenisTransaksi.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Jenis Transaksi tidak boleh kosong", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
        }
    }

}