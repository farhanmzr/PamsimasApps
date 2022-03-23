package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.detailpelanggan

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pamsimas.pamsimasapps.databinding.ItemRvRiwayatPemakaianBinding
import com.pamsimas.pamsimasapps.models.Meteran
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DetailPelangganAdapter: RecyclerView.Adapter<DetailPelangganViewHolder>() {

    private var listPelanggan = ArrayList<Meteran>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListMeteran(meteran: List<Meteran>?) {
        if (meteran == null) return
        this.listPelanggan.clear()
        this.listPelanggan.addAll(meteran)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailPelangganViewHolder {
        val itemRvDetailPemakaian =
            ItemRvRiwayatPemakaianBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return DetailPelangganViewHolder(itemRvDetailPemakaian)
    }

    override fun onBindViewHolder(holder: DetailPelangganViewHolder, position: Int) {
        val meteran = listPelanggan[position]
        holder.bind(meteran)
    }

    override fun getItemCount(): Int = listPelanggan.size

}