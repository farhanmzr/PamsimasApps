package com.pamsimas.pamsimasapps.ui.dashboard.home

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.FragmentHomeBinding
import com.pamsimas.pamsimasapps.models.Kegiatan
import com.pamsimas.pamsimasapps.models.Users
import com.pamsimas.pamsimasapps.ui.dashboard.MainActivity
import com.pamsimas.pamsimasapps.ui.dashboard.MainViewModel
import com.pamsimas.pamsimasapps.ui.dashboard.home.iuranbulanan.IuranBulananFragment
import com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas.KegiatanPamsimasAdapter
import com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas.KegiatanPamsimasClickCallback
import com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas.KegiatanPamsimasFragment
import com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas.detail.DetailKegiatanPamsimasFragment
import com.pamsimas.pamsimasapps.ui.dashboard.home.laporankeuangan.LaporanKeuanganFragment
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.PelangganActivity
import com.pamsimas.pamsimasapps.ui.dashboard.home.tentangpamsimas.TentangPamsimasFragment
import com.pamsimas.pamsimasapps.utils.AppConstants.BELUM_BAYAR
import com.pamsimas.pamsimasapps.utils.ProgressDialogHelper
import com.pamsimas.pamsimasapps.utils.RvDecoration
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.*

@ExperimentalCoroutinesApi
class HomeFragment : Fragment(), View.OnClickListener, KegiatanPamsimasClickCallback  {

    private lateinit var binding : FragmentHomeBinding

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var usersDataProfile: Users
    private val kegiatanAdapter = KegiatanPamsimasAdapter(this)

    private val firestoreRef: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val pelangganRef: CollectionReference = firestoreRef.collection("customers")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getProfileData()

    }

    private fun getProfileData(){
        mainViewModel.getUserData()
            .observe(viewLifecycleOwner) { userProfile ->
                if (userProfile != null) {
                    usersDataProfile = userProfile
                    initView(usersDataProfile)
                    initSizePelanggan()
                    setDataKegiatanRv()
                }
                Log.d("ViewModelProfile: ", userProfile.toString())
            }
    }

    @SuppressLint("SetTextI18n")
    private fun initSizePelanggan() {
        pelangganRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful){
              val count = task.result?.size()
                binding.tvPelangganAktif.text = "${count.toString()} pelanggan"
            }
        }
        pelangganRef.whereEqualTo("statusTagihan", BELUM_BAYAR).get().addOnCompleteListener { task ->
            if (task.isSuccessful){
                val count = task.result?.size()
                binding.tvBelumDibayar.text = "${count.toString()} pelanggan"
            }
        }
    }

    private fun initView(usersDataProfile: Users) {
        with(binding){
            tvUsername.text = usersDataProfile.name
            tvLihatSemua.setOnClickListener(this@HomeFragment)
            kategoriInputMeteran.setOnClickListener(this@HomeFragment)
            kategoriTentangPamsimas.setOnClickListener(this@HomeFragment)
            kategoriBayarIuran.setOnClickListener(this@HomeFragment)
            kategoriLaporanKeuangan.setOnClickListener(this@HomeFragment)
        }

    }

    private fun setDataKegiatanRv() {
        showProgressBar(true)
        mainViewModel.getListKegiatan()
            .observe(viewLifecycleOwner) { dataKegiatan ->
                if (dataKegiatan != null && dataKegiatan.isNotEmpty()) {
                    showProgressBar(false)
                    kegiatanAdapter.setListKegiatan(dataKegiatan)
                    setKegiatanAdapter()
                } else {
                    showProgressBar(false)
                }
            }
    }

    private fun setKegiatanAdapter() {
        with(binding.rvKegiatanPamsimas) {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            val snapHelper: SnapHelper = PagerSnapHelper()
            onFlingListener = null
            snapHelper.attachToRecyclerView(this)
            val mRvDecoration = RvDecoration()
            addItemDecoration(mRvDecoration)
            adapter = kegiatanAdapter
        }
    }

    private fun showProgressBar(state: Boolean) {
        if (state) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.tv_lihat_semua -> gotoKegiatanPamsimas()
            R.id.kategori_input_meteran -> gotoPelanggan()
            R.id.kategori_tentang_pamsimas -> gotoTentangPamsimas()
            R.id.kategori_bayar_iuran -> gotoIuranBulanan()
            R.id.kategori_laporan_keuangan -> gotoLaporanKeuangan()
        }
    }

    private fun gotoLaporanKeuangan() {
        val mLaporanKeuangan = LaporanKeuanganFragment()
        setCurrentFragment(mLaporanKeuangan)
    }

    private fun gotoIuranBulanan() {
        val mIuranBulanan = IuranBulananFragment()
        setCurrentFragment(mIuranBulanan)
    }

    private fun gotoPelanggan() {
        val intent = Intent(requireActivity(), PelangganActivity::class.java)
        startActivity(intent)
    }

    private fun gotoTentangPamsimas() {
        val mTentangPamsimas = TentangPamsimasFragment()
        setCurrentFragment(mTentangPamsimas)
    }

    private fun gotoKegiatanPamsimas() {
        val mKegiatanPamsimas = KegiatanPamsimasFragment()
        setCurrentFragment(mKegiatanPamsimas)
    }

    private fun setCurrentFragment(fragment: Fragment) {
        parentFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.host_fragment_activity_main, fragment, MainActivity.CHILD_FRAGMENT)
        }
    }

    override fun onItemClicked(data: Kegiatan) {
        val mDetailKegiatanPamsimas = DetailKegiatanPamsimasFragment()
        val mBundle = Bundle()
        mBundle.putParcelable(DetailKegiatanPamsimasFragment.EXTRA_KEGIATAN_DATA, data)
        mBundle.putBoolean(DetailKegiatanPamsimasFragment.FROM_HOME, true)
        mDetailKegiatanPamsimas.arguments = mBundle
        setCurrentFragment(mDetailKegiatanPamsimas)

    }
    
}