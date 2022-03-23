package com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.pamsimas.pamsimasapps.databinding.ItemRvKegiatanPamsimasBinding
import com.pamsimas.pamsimasapps.models.Kegiatan
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class KegiatanPamsimasAdapter(private val mKegiatanPamsimasClickCallback: KegiatanPamsimasClickCallback): RecyclerView.Adapter<KegiatanPamsimasViewHolder>() {

    private var listKegiatan = ArrayList<Kegiatan>()

    @SuppressLint("NotifyDataSetChanged")
    fun setListKegiatan(kegiatans: List<Kegiatan>?) {
        if (kegiatans == null) return
        this.listKegiatan.clear()
        this.listKegiatan.addAll(kegiatans)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KegiatanPamsimasViewHolder {
        val itemRvHomeBinding =
            ItemRvKegiatanPamsimasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return KegiatanPamsimasViewHolder(mKegiatanPamsimasClickCallback, itemRvHomeBinding)
    }

    override fun onBindViewHolder(holder: KegiatanPamsimasViewHolder, position: Int) {
        val kegiatans = listKegiatan[position]
        holder.bind(kegiatans)
    }

    override fun getItemCount(): Int = listKegiatan.size

}