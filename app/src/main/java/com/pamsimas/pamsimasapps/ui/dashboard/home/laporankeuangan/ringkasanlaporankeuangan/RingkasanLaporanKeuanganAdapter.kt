package com.pamsimas.pamsimasapps.ui.dashboard.home.laporankeuangan.ringkasanlaporankeuangan

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pamsimas.pamsimasapps.databinding.ItemRvRingkasanLaporanBinding
import com.pamsimas.pamsimasapps.models.RingkasanLaporan

class RingkasanLaporanKeuanganAdapter: RecyclerView.Adapter<RingkasanLaporanKeuanganViewHolder>() {

    private var listRingkasanLaporan = ArrayList<RingkasanLaporan>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListRingkasanLaporan(laporans: List<RingkasanLaporan>?) {
        if (laporans == null) return
        this.listRingkasanLaporan.clear()
        this.listRingkasanLaporan.addAll(laporans)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RingkasanLaporanKeuanganViewHolder {
        val itemRvRingkasan =
            ItemRvRingkasanLaporanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RingkasanLaporanKeuanganViewHolder(itemRvRingkasan)
    }

    override fun onBindViewHolder(holder: RingkasanLaporanKeuanganViewHolder, position: Int) {
        val kegiatans = listRingkasanLaporan[position]
        holder.bind(kegiatans)
    }

    override fun getItemCount(): Int = listRingkasanLaporan.size

}