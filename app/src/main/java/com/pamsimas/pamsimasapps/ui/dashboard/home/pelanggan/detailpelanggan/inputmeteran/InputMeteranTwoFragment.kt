package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.detailpelanggan.inputmeteran

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentInputMeteranOneBinding
import com.pamsimas.pamsimasapps.databinding.FragmentInputMeteranTwoBinding
import com.pamsimas.pamsimasapps.models.Meteran
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.PelangganViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.detailpelanggan.inputmeteran.InputMeteranTwoFragment.Companion.EXTRA_METERAN_DATA
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.detailpelanggan.inputmeteran.otp.InputMeteranOtpFragment
import com.pamsimas.pamsimasapps.ui.login.register.RegisterTwoFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class InputMeteranTwoFragment : Fragment() {

    private lateinit var binding: FragmentInputMeteranTwoBinding

    private lateinit var meteranData: Meteran
    private var uriImagePath: Uri? = null

    companion object {
        const val EXTRA_METERAN_DATA = "extra_meteran_data"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputMeteranTwoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        if (bundle != null) {
            meteranData = bundle.getParcelable(EXTRA_METERAN_DATA)!!
        }

        initView()

    }

    private fun initView() {
        val getImage =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { uriImage ->
                if (uriImage.path != null) {
                    uriImagePath = uriImage
                    binding.imgMeteran.setImageURI(uriImagePath)
                    binding.linearImgChange.visibility = View.GONE
                }
            }
        binding.apply {
            icBack.setOnClickListener {
                requireActivity().supportFragmentManager.popBackStackImmediate()
            }
            btnNext.setOnClickListener {
                if (uriImagePath == null) {
                    Toast.makeText(requireContext(), "Gambar Kegiatan tidak boleh kosong.", Toast.LENGTH_SHORT).show()
                }
                if (validateInput()){
                    verificationOTP()
                }
            }
            tvUnggahFoto.setOnClickListener {
                getImage.launch(arrayOf("image/*"))
            }
        }
    }

    private fun verificationOTP() {

        val desc = binding.etKeterangan.text.toString().trim()

        val meteranInfo = Meteran(meteranId = meteranData.meteranId,
        customerId = meteranData.customerId,
        standAwal = meteranData.standAwal,
        standAkhir = meteranData.standAkhir,
        meteranSegel = meteranData.meteranSegel,
        dateInput = meteranData.dateInput,
        meteranDesc = desc,
        uriPath = uriImagePath,
        )
        val mInputMeteranTwo = InputMeteranOtpFragment()
        val mBundle = Bundle()
        mBundle.putParcelable(InputMeteranOtpFragment.EXTRA_METERAN_DATA, meteranInfo)
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
        val desc = binding.etKeterangan.text.toString().trim()

        return when {
            desc.isEmpty() -> {
                binding.etKeterangan.error = "Keterangan tidak boleh kosong"
                false
            }
            else -> {
                true
            }
        }
    }

}