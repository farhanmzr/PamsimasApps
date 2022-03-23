package com.pamsimas.pamsimasapps.ui.dashboard.home.iuranbulanan.detailiuranbulanan.riwayatiuran

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.ItemRvDaftarPelangganBinding
import com.pamsimas.pamsimasapps.databinding.ItemRvIuranBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.models.Iuran
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.detailpelanggan.DetailPelangganFragment
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class RiwayatIuranViewHolder(private val binding: ItemRvIuranBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Iuran) {
        with(binding) {

            tvDate.text = (data.dateInput)
            tvStandAkhir.text = (data.standAkhir)
            tvTotalBiaya.text = (data.totalBiaya)

            with(itemView) {
                setOnClickListener {

                }
            }

        }
    }


}