package com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.recyclerview.widget.RecyclerView
import com.pamsimas.pamsimasapps.R
import com.pamsimas.pamsimasapps.databinding.ItemRvKegiatanPamsimasBinding
import com.pamsimas.pamsimasapps.models.Kegiatan
import com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas.detail.DetailKegiatanPamsimasFragment
import com.pamsimas.pamsimasapps.utils.loadImage
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class KegiatanPamsimasViewHolder (private val mKegiatanPamsimasCallback : KegiatanPamsimasClickCallback, private val binding: ItemRvKegiatanPamsimasBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Kegiatan) {
        with(binding) {

            imgKegiatan.loadImage(data.kegiatanPicture)
            tvDate.text = (data.kegiatanDate)
            tvName.text = (data.kegiatanDesc)

            with(itemView) {
                setOnClickListener {
                    mKegiatanPamsimasCallback.onItemClicked(data)
                }
            }

        }
    }
}