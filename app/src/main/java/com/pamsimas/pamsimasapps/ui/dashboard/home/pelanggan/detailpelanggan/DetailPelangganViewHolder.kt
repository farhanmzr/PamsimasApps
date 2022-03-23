package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.detailpelanggan

import androidx.recyclerview.widget.RecyclerView
import com.pamsimas.pamsimasapps.databinding.ItemRvRiwayatPemakaianBinding
import com.pamsimas.pamsimasapps.models.Meteran
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DetailPelangganViewHolder(private val binding: ItemRvRiwayatPemakaianBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Meteran) {
        with(binding) {

            tvDate.text = (data.dateInput)
            tvStandAwal.text = (data.standAwal)
            tvStandAkhir.text = (data.standAkhir)

        }
    }
}