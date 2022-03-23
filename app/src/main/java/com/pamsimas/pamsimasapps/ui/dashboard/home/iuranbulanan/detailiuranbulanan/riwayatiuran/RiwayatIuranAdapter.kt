package com.pamsimas.pamsimasapps.ui.dashboard.home.iuranbulanan.detailiuranbulanan.riwayatiuran

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pamsimas.pamsimasapps.databinding.ItemRvDaftarPelangganBinding
import com.pamsimas.pamsimasapps.databinding.ItemRvIuranBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.models.Iuran
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.daftarpelanggan.DaftarPelangganViewHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class RiwayatIuranAdapter: RecyclerView.Adapter<RiwayatIuranViewHolder>() {

    private var listIuran = ArrayList<Iuran>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListIuran(orders: List<Iuran>?) {
        if (orders == null) return
        this.listIuran.clear()
        this.listIuran.addAll(orders)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiwayatIuranViewHolder {
        val itemRvIuran =
            ItemRvIuranBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return RiwayatIuranViewHolder(itemRvIuran)
    }

    override fun onBindViewHolder(holder: RiwayatIuranViewHolder, position: Int) {
        val orders = listIuran[position]
        holder.bind(orders)
    }

    override fun getItemCount(): Int = listIuran.size

}