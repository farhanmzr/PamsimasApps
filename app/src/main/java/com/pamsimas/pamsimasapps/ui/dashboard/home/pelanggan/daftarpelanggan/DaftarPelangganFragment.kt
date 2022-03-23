package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.daftarpelanggan

import android.app.Dialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentDaftarPelangganBinding
import com.pamsimas.pamsimasapps.models.Pamsimas
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.PelangganViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.tambahpelanggan.TambahPelangganFragment
import com.pamsimas.pamsimasapps.utils.AppConstants
import com.pamsimas.pamsimasapps.utils.AppConstants.DATA_ID
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import com.pamsimas.pamsimasapps.utils.getMonthString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
class DaftarPelangganFragment : Fragment() {

    private lateinit var binding : FragmentDaftarPelangganBinding
    private val pelangganViewModel: PelangganViewModel by activityViewModels()
    private lateinit var pamsimasData: Pamsimas
    private lateinit var progressDialog : Dialog

    private val firestoreRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val dataRef: CollectionReference = firestoreRef.collection("data")
    private val customerRef: CollectionReference = firestoreRef.collection("customers")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDaftarPelangganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().actionBar?.hide()

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())

        getDataPamsimas()
        initTabLayout()

    }

    private fun getDataPamsimas() {

        progressDialog.show()

        val now = Calendar.getInstance(TimeZone.getDefault())
        val month = now.get(Calendar.MONTH)
        val year = now.get(Calendar.YEAR)
        val date = "${getMonthString(month)} $year"

        pelangganViewModel.getDataPamsimas()
            .observe(viewLifecycleOwner) { pamsimas ->
                if (pamsimas != null) {
                    pamsimasData = pamsimas
                    if (pamsimasData.inputMeteranBulan != date){
                        resetStatusInputCustomer(date)
                    } else {
                        progressDialog.dismiss()
                    }
                }
            }
    }

    private fun resetStatusInputCustomer(date: String) {

        dataRef.document(DATA_ID)
            .update("inputMeteranBulan", date)
            .addOnSuccessListener {
                resetStatus()
                Log.i("Update", "Value Updated")
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Error In Updating Details: " + e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun resetStatus() {
        customerRef.get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list: MutableList<String> = ArrayList()
                    for (document in task.result!!) {
                        list.add(document.id)
                        Log.e("list", list.toString())
                    }
                    for (k in 0 until list.size) {
                        customerRef.document(list[k])
                            .update("statusInput", AppConstants.BELUM_DIINPUT)
                            .addOnSuccessListener {
                                progressDialog.dismiss()
                                Log.i("Update", "Value Updated")
                            }
                            .addOnFailureListener { e ->
                                progressDialog.dismiss()
                                Toast.makeText(
                                    requireContext(),
                                    "Error In Updating Details: " + e.message,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                    }
                    Log.d(ContentValues.TAG, list.toString())
                } else {
                    progressDialog.dismiss()
                    Log.d(ContentValues.TAG, "Error getting documents: ", task.exception)
                }
            }
    }


    private fun initTabLayout() {

        binding.apply {
            btnTambahPelanggan.setOnClickListener {
                val tambahPelangganFragment = TambahPelangganFragment()
                val mFragmentManager = parentFragmentManager
                mFragmentManager.commit {
                    addToBackStack(null)
                    replace(
                        R.id.host_fragment_activity_pelanggan,
                        tambahPelangganFragment
                    )
                }
            }
            icBack.setOnClickListener {
                activity?.onBackPressed()
            }
        }


        val sectionAdapter = SectionDaftarPelangganAdapter(this)
        binding.viewPager.adapter = sectionAdapter
        binding.viewPager.isSaveEnabled = false

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            when (position) {
                0 -> tab.text = "Belum Diinput"
                1 -> tab.text = "Sudah Diinput"
            }
        }.attach()
    }



}