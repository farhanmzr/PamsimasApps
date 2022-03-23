package com.pamsimas.pamsimasapps.ui.dashboard.home.laporankeuangan.ringkasanlaporankeuangan

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.ItemRvRingkasanLaporanBinding
import com.pamsimas.pamsimasapps.models.RingkasanLaporan
import com.pamsimas.pamsimasapps.utils.AppConstants.PEMASUKAN
import com.pamsimas.pamsimasapps.utils.AppConstants.PENGELUARAN
import com.pamsimas.pamsimasapps.utils.PriceFormatHelper

class RingkasanLaporanKeuanganViewHolder(private val binding: ItemRvRingkasanLaporanBinding) :
    RecyclerView.ViewHolder(binding.root) {
    @SuppressLint("SetTextI18n")
    fun bind(data: RingkasanLaporan) {
        with(binding) {

            val price = PriceFormatHelper.getPriceFormat(data.ringkasanPrice!!)

            if (data.ringkasanTipe == PEMASUKAN){
                icJenisLaporan.setBackgroundResource(R.drawable.ic_pemasukan)
                tvRingkasanPrice.apply {
                    text = "+ $price"
                    setTextColor(ContextCompat.getColor(context,R.color.ijo))
                }
            } else if (data.ringkasanTipe == PENGELUARAN) {
                icJenisLaporan.setBackgroundResource(R.drawable.ic_pengeluaran)
                tvRingkasanPrice.apply {
                    text = "- $price"
                    setTextColor(ContextCompat.getColor(context,R.color.merah))
                }
            }

            tvRingkasanKeterangan.text = data.ringkasanKeterangan
            tvRingkasanKategori.text = data.ringkasanKategori

            with(itemView) {
                setOnClickListener {

                }
            }

        }
    }
}