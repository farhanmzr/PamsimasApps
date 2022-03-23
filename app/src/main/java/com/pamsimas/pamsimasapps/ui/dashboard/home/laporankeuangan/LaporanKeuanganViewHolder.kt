package com.pamsimas.pamsimasapps.ui.dashboard.home.laporankeuangan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.ItemRvKegiatanPamsimasBinding
import com.pamsimas.pamsimasapps.databinding.ItemRvLaporanKeuanganBinding
import com.pamsimas.pamsimasapps.models.Kegiatan
import com.pamsimas.pamsimasapps.models.Laporan
import com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas.KegiatanPamsimasClickCallback
import com.pamsimas.pamsimasapps.ui.dashboard.home.laporankeuangan.ringkasanlaporankeuangan.RingkasanLaporanKeuanganFragment
import com.pamsimas.pamsimasapps.utils.PriceFormatHelper
import com.pamsimas.pamsimasapps.utils.loadImage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class LaporanKeuanganViewHolder(private val binding: ItemRvLaporanKeuanganBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Laporan) {
        with(binding) {

            tvLaporanDate.text = data.laporanDate
            tvLaporanPemasukan.text = PriceFormatHelper.getPriceFormat(data.laporanPemasukan!!)
            tvLaporanPengeluaran.text = PriceFormatHelper.getPriceFormat(data.laporanPengeluaran!!)

            with(itemView) {
                setOnClickListener {
                    val mRingkasanLaporanKeuangan = RingkasanLaporanKeuanganFragment()
                    val mBundle = Bundle()
                    mBundle.putString(RingkasanLaporanKeuanganFragment.EXTRA_LAPORAN_ID, data.laporanId)
                    mBundle.putString(RingkasanLaporanKeuanganFragment.EXTRA_LAPORAN_DATE, data.laporanDate)
                    mRingkasanLaporanKeuangan.arguments = mBundle
                    val manager: FragmentManager =
                        (context as AppCompatActivity).supportFragmentManager
                    manager.commit {
                        addToBackStack(null)
                        replace(
                            R.id.host_fragment_activity_main,
                            mRingkasanLaporanKeuangan
                        )
                    }
                }
            }

        }
    }
}