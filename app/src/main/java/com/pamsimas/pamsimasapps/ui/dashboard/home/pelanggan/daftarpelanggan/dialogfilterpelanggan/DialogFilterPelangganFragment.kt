package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.daftarpelanggan.dialogfilterpelanggan

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentDialogFilterPelangganBinding
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.daftarpelanggan.ContentDaftarPelangganFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DialogFilterPelangganFragment : DialogFragment() {

    private var mView: View? = null
    private var _binding: FragmentDialogFilterPelangganBinding? = null
    private val binding get() = _binding!!
    private var optionDialogListener: OnOptionDialogListener? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.run {
            //initiate the binding here and pass the root to the dialog view
            _binding = FragmentDialogFilterPelangganBinding.inflate(layoutInflater).apply {

                btnPilihKategori.setOnClickListener {
                    val checkedRadioButtonId = binding.rgKategori.checkedRadioButtonId
                    if (checkedRadioButtonId != 1) {
                        var kategori: String? = null
                        when (checkedRadioButtonId) {
                            R.id.a_to_z -> kategori = binding.aToZ.text.toString().trim()
                            R.id.z_to_a -> kategori = binding.zToA.text.toString().trim()
                            R.id.tagihan_sudah_dibayar -> kategori = binding.tagihanSudahDibayar.text.toString().trim()
                            R.id.tagihan_belum_dibayar -> kategori = binding.tagihanBelumDibayar.text.toString().trim()
                        }
                        optionDialogListener?.onOptionChosen(kategori)
                        dialog?.dismiss()
                    }
                }
            }
            AlertDialog.Builder(this).apply {
                setView(binding.root)
            }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        val fragment = parentFragment

        if (fragment is ContentDaftarPelangganFragment) {
            val homeFragment = fragment
            this.optionDialogListener = homeFragment.optionDialogListener
        }
    }

    override fun onDetach() {
        super.onDetach()
        this.optionDialogListener = null
    }


    interface OnOptionDialogListener {
        fun onOptionChosen(filter: String?)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mView = null
    }

}