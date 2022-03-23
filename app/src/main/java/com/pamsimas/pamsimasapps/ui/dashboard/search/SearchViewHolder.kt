package com.pamsimas.pamsimasapps.ui.dashboard.search

import androidx.recyclerview.widget.RecyclerView
import com.pamsimas.pamsimasapps.databinding.ItemRvDaftarPelangganBinding
import com.pamsimas.pamsimasapps.models.Customers
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class SearchViewHolder(private val mSearchClickCallback: SearchClickCallback, private val binding: ItemRvDaftarPelangganBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Customers) {
        with(binding) {

            tvNamaPelanggan.text = (data.customerName)
            tvNik.text = (data.customerNIK)

            with(itemView) {
                setOnClickListener {
                    mSearchClickCallback.onItemClicked(data)
                }
            }

        }
    }


}