package com.pamsimas.pamsimasapps.ui.dashboard.home.pelanggan.daftarpelanggan

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class SectionDaftarPelangganAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun createFragment(position: Int): Fragment {
        return ContentDaftarPelangganFragment.newInstance(position + 1)
    }

    override fun getItemCount(): Int {
        return 2
    }
}