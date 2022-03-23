package com.pamsimas.pamsimasapps.ui.dashboard.search

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pamsimas.pamsimasapps.databinding.ItemRvDaftarPelangganBinding
import com.pamsimas.pamsimasapps.models.Customers
import com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.daftarpelanggan.DaftarPelangganViewHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class SearchAdapter(private val mSearchClickCallback: SearchClickCallback): RecyclerView.Adapter<SearchViewHolder>() {

    private var listPelanggan = ArrayList<Customers>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListSearch(orders: List<Customers>?) {
        if (orders == null) return
        this.listPelanggan.clear()
        this.listPelanggan.addAll(orders)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val itemRvDaftarPelanggan =
            ItemRvDaftarPelangganBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return SearchViewHolder(mSearchClickCallback, itemRvDaftarPelanggan)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val orders = listPelanggan[position]
        holder.bind(orders)
    }

    override fun getItemCount(): Int = listPelanggan.size

}