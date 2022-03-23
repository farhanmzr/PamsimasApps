package com.pamsimas.pamsimasapps.ui.dashboard.home.iuranbulanan.detailiuranbulanan

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pamsimas.pamsimasapps.ui.dashboard.home.iuranbulanan.detailiuranbulanan.ringkasaniuran.RingkasanIuranFragment
import com.pamsimas.pamsimasapps.ui.dashboard.home.iuranbulanan.detailiuranbulanan.riwayatiuran.RiwayatIuranFragment

class SectionDetailIuranAdapter (fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return RingkasanIuranFragment()
            1 -> return RiwayatIuranFragment()
        }
        return Fragment()
    }

}