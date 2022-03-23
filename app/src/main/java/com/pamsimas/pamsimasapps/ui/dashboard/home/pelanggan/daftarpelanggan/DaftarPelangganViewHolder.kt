package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.daftarpelanggan

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.ItemRvDaftarPelangganBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas.KegiatanPamsimasClickCallback
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.detailpelanggan.DetailPelangganFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DaftarPelangganViewHolder(private val binding: ItemRvDaftarPelangganBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Customers) {
        with(binding) {

            tvNamaPelanggan.text = (data.customerName)
            tvNik.text = (data.customerNIK)

            with(itemView) {
                setOnClickListener {
                    val mDetailPelanggan = DetailPelangganFragment()
                    val mBundle = Bundle()
                    mBundle.putParcelable(DetailPelangganFragment.EXTRA_PELANGGAN_DATA, data)
                    mDetailPelanggan.arguments = mBundle
                    val manager: FragmentManager =
                        (context as AppCompatActivity).supportFragmentManager
                    manager.commit {
                        addToBackStack(null)
                        replace(R.id.host_fragment_activity_pelanggan, mDetailPelanggan)
                    }
                }
            }

        }
    }
}