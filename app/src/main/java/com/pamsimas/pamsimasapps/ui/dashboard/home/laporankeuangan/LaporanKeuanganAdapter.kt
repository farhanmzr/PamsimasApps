package com.pamsimas.pamsimasapps.ui.dashboard.home.laporankeuangan

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pamsimas.pamsimasapps.databinding.ItemRvLaporanKeuanganBinding
import com.pamsimas.pamsimasapps.models.Laporan
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class LaporanKeuanganAdapter: RecyclerView.Adapter<LaporanKeuanganViewHolder>() {

    private var listLaporan = ArrayList<Laporan>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListLaporan(laporans: List<Laporan>?) {
        if (laporans == null) return
        this.listLaporan.clear()
        this.listLaporan.addAll(laporans)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaporanKeuanganViewHolder {
        val itemRvLaporan =
            ItemRvLaporanKeuanganBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LaporanKeuanganViewHolder(itemRvLaporan)
    }

    override fun onBindViewHolder(holder: LaporanKeuanganViewHolder, position: Int) {
        val kegiatans = listLaporan[position]
        holder.bind(kegiatans)
    }

    override fun getItemCount(): Int = listLaporan.size

}