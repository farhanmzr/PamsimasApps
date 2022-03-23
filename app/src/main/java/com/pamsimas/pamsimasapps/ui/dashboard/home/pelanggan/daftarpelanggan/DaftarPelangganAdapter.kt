package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.daftarpelanggan

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pamsimas.pamsimasapps.databinding.ItemRvDaftarPelangganBinding
import com.pamsimas.pamsimasapps.models.Customers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class DaftarPelangganAdapter: RecyclerView.Adapter<DaftarPelangganViewHolder>() {

    private var listPelanggan = ArrayList<Customers>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListCustomers(orders: List<Customers>?) {
        if (orders == null) return
        this.listPelanggan.clear()
        this.listPelanggan.addAll(orders)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarPelangganViewHolder {
        val itemRvDaftarPelanggan =
            ItemRvDaftarPelangganBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return DaftarPelangganViewHolder(itemRvDaftarPelanggan)
    }

    override fun onBindViewHolder(holder: DaftarPelangganViewHolder, position: Int) {
        val orders = listPelanggan[position]
        holder.bind(orders)
    }

    override fun getItemCount(): Int = listPelanggan.size

}