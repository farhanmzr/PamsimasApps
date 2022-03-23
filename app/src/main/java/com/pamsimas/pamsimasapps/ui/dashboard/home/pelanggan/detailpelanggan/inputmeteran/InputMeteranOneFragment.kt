package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.detailpelanggan.inputmeteran

import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentInputMeteranOneBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.models.Meteran
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.PelangganViewModel
import com.pamsimas.pamsimasapps.utils.AppConstants.BELUM_DIINPUT
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import com.pamsimas.pamsimasapps.utils.getMonthString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*


@ExperimentalCoroutinesApi
class InputMeteranOneFragment : Fragment() {

    private lateinit var binding: FragmentInputMeteranOneBinding

    private val pelangganViewModel: PelangganViewModel by activityViewModels()

    private lateinit var customerData: Customers

    private var segelMeteran: String? = null
    private var dateInput: String? = null
    private var meteranId: String? = null
    private val timeOut: Long = 1000
    private lateinit var progressDialog : Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputMeteranOneBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        progressDialog.show()

        customerData = Customers()
        pelangganViewModel.getCustomerData()
            .observe(viewLifecycleOwner) { customers ->
                if (customers != null) {
                    customerData = customers
                    checkDatabase(customerData)
                }
            }

        initView()
    }

    private fun checkDatabase(customerData: Customers) {
        //getMonthYearNow
        val now = Calendar.getInstance(TimeZone.getDefault())
        val month = now.get(Calendar.MONTH)
        val year = now.get(Calendar.YEAR)
        val date = "${getMonthString(month)} $year"
        val dateNotSpace = "${getMonthString(month)}$year"
        meteranId = dateNotSpace + customerData.customerId
        Log.e("meteranId", meteranId!!)
        dateInput = date
        Log.e("monthYear", date)

        //Check to database
        val firestoreRef: FirebaseFirestore = FirebaseFirestore.getInstance()
        val customerRef: CollectionReference = firestoreRef.collection("customers")

        //delayView
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            firestoreRef.collection("customers").document(customerData.customerId.toString()).collection("meteran")
                .whereEqualTo("dateInput", date).get().addOnSuccessListener { documents ->
                    if (documents.isEmpty) {

                        customerRef.document(customerData.customerId.toString())
                            .update("statusInput", BELUM_DIINPUT)
                            .addOnCompleteListener {
                                progressDialog.dismiss()
                                showView(true)
                                binding.tvTitle.text = "Input Data Meteran pada " + "$date"
                            } .addOnFailureListener { error ->
                                val errorMessage = error.message.toString()
                                Log.d("ErrorUpdateInput: ", errorMessage)
                            }
                    } else {
                        progressDialog.dismiss()
                        showView(false)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w(TAG, "Error getting documents: ", exception)
                }
        }, timeOut)
    }

    private fun initView() {

        //binding
        binding.apply {

            icBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
            btnNext.setOnClickListener {
                if (validateInput()){
                    nextInputMeteran()
                }
            }

            rgSegel.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.rb_ada -> segelMeteran = binding.rbAda.text.toString()
                    R.id.rb_tidak_ada -> segelMeteran = binding.rbTidakAda.text.toString()
                }
            }

        }

    }

    private fun nextInputMeteran() {
        val standAwal = binding.etStandAwal.text.toString().trim()
        val standAkhir = binding.etStandAkhir.text.toString().trim()

        val meteranInfo = Meteran( meteranId = meteranId,
        customerId = customerData.customerId,
        standAwal = standAwal,
        standAkhir = standAkhir,
        meteranSegel = segelMeteran,
        dateInput = dateInput
        )
        val mInputMeteranTwo = InputMeteranTwoFragment()
        val mBundle = Bundle()
        mBundle.putParcelable(InputMeteranTwoFragment.EXTRA_METERAN_DATA, meteranInfo)
        mInputMeteranTwo.arguments = mBundle

        val mFragmentManager = parentFragmentManager
        mFragmentManager.commit {
            addToBackStack(null)
            replace(
                R.id.host_fragment_activity_pelanggan,
                mInputMeteranTwo
            )
        }
    }

    private fun validateInput(): Boolean {
        val standAwal = binding.etStandAwal.text.toString().trim()
        val standAkhir = binding.etStandAkhir.text.toString().trim()

        return when {
            standAwal.isEmpty() -> {
                binding.etStandAwal.error = "Stand Awal tidak boleh kosong"
                false
            }
            standAkhir.isEmpty() -> {
                binding.etStandAkhir.error = "Stand Akhir tidak boleh kosong"
                false
            }
            segelMeteran.isNullOrEmpty() -> {
                Toast.makeText(requireContext(), "Segel Meteran tidak boleh kosong", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                true
            }
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