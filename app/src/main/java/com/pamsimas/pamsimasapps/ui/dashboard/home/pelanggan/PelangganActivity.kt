package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.models.Pamsimas
import com.pamsimas.pamsimasapps.models.Users
import com.pamsimas.pamsimasapps.ui.dashboard.MainActivity
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.daftarpelanggan.DaftarPelangganFragment
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.detailpelanggan.DetailPelangganFragment
import com.pamsimas.pamsimasapps.ui.login.LoginFragment
import com.pamsimas.pamsimasapps.utils.AppConstants.DATA_ID
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class PelangganActivity : AppCompatActivity() {

    private val pelangganViewModel: PelangganViewModel by viewModels()

    private lateinit var pamsimasData: Pamsimas

    companion object {
        const val EXTRA_DATA = "extra_data"
        const val EXTRA_HOME = "extra_home"
    }

    private lateinit var customersData: Customers

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pelanggan)

        setPamsimasId(DATA_ID)

        if (intent.hasExtra(EXTRA_HOME)){
            customersData = intent.getParcelableExtra(EXTRA_DATA)!!
            val mDetailPelangganFragment = DetailPelangganFragment()
            val mBundle = Bundle()
            mBundle.putParcelable(DetailPelangganFragment.EXTRA_PELANGGAN_DATA, customersData)
            mBundle.putBoolean(DetailPelangganFragment.EXTRA_FROM_HOME, true)
            mDetailPelangganFragment.arguments = mBundle
            supportFragmentManager.commit {
                replace(R.id.host_fragment_activity_pelanggan, mDetailPelangganFragment)
            }
        } else {
            val daftarPelangganFragment = DaftarPelangganFragment()
            supportFragmentManager.commit {
                replace(R.id.host_fragment_activity_pelanggan, daftarPelangganFragment)
            }
        }

    }

    private fun setPamsimasId(dataId: String) {
        pelangganViewModel.setDataPamsimas(dataId)
            .observe(this) { pamsimas ->
                if (pamsimas != null) {
                    pamsimasData = pamsimas
                }
            }
    }

}