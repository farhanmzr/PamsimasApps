package com.pamsimas.pamsimasapps.ui.dashboard.home.iuranbulanan

import android.app.Dialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentIuranBulananBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.iuranbulanan.detailiuranbulanan.DetailIuranFragment
import com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas.detail.DetailKegiatanPamsimasFragment
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class IuranBulananFragment : Fragment() {

    private lateinit var binding: FragmentIuranBulananBinding


    private val timeOut: Long = 1500
    private lateinit var progressDialog : Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentIuranBulananBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialogHelper.progressDialog(requireContext())
        val bottomNav: BottomNavigationView =
            requireActivity().findViewById(R.id.bottom_navigation)
        bottomNav.visibility = View.GONE

        binding.apply {

            btnSearch.isEnabled = false
            //search
            fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
                this.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                        binding.btnSearch.isEnabled = false
                    }

                    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                        binding.btnSearch.isEnabled = false
                    }

                    override fun afterTextChanged(editable: Editable?) {
                        binding.btnSearch.isEnabled = true
                        afterTextChanged.invoke(editable.toString())
                    }
                })
            }

            etSearch.afterTextChanged { searchQuery ->
                binding.btnSearch.isEnabled = true
                binding.btnSearch.setOnClickListener {
                    progressDialog.show()
                    showResult(searchQuery)
                }
            }

            //back
            icBack.setOnClickListener {
                bottomNav.visibility = View.VISIBLE
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
        }

    }

    private fun showResult(searchQuery: String) {
        val firestoreRef: FirebaseFirestore = FirebaseFirestore.getInstance()
        //delayView
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            firestoreRef.collection("customers").whereEqualTo("customerName", searchQuery)
                .get().addOnSuccessListener { documents ->
                    if (!documents.isEmpty){
                        for (document in documents) {
                            Log.d(TAG, "${document.id} => ${document.data}")
                            progressDialog.dismiss()
                            gotoDetail(document.id)
                            showView(true)
                        }
                    } else {
                        progressDialog.dismiss()
                        showView(false)
                    }

                }
                .addOnFailureListener { exception ->
                    Log.w(ContentValues.TAG, "Error getting documents: ", exception)
                    progressDialog.dismiss()
                }
        }, timeOut)
    }

    private fun gotoDetail(id: String) {
        val mDetailIuran = DetailIuranFragment()
        val mBundle = Bundle()
        mBundle.putString(DetailIuranFragment.EXTRA_ID, id)
        mDetailIuran.arguments = mBundle

        val mFragmentManager = parentFragmentManager
        mFragmentManager.commit {
            addToBackStack(null)
            replace(
                R.id.host_fragment_activity_main,
                mDetailIuran
            )
        }
    }

    private fun showView(state: Boolean) {
        if (state){
            binding.apply {
                cardView.visibility = View.GONE
                linearEmpty.visibility = View.GONE
            }
        } else {
            binding.apply {
                cardView.visibility = View.VISIBLE
                linearEmpty.visibility = View.VISIBLE
            }
        }
    }

}