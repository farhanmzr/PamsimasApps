package com.pamsimas.pamsimasapps.ui.dashboard.home.tentangpamsimas

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.pamsimas.pamsimasapps.ui.dashboard.home.tentangpamsimas.aset.AsetPamsimasFragment
import com.pamsimas.pamsimasapps.ui.dashboard.home.tentangpamsimas.latarbelakang.LatarBelakangPamsimasFragment
import com.pamsimas.pamsimasapps.ui.dashboard.home.tentangpamsimas.profil.ProfilePamsimasFragment

class SectionTentangPamsimasAdapter (fragment: Fragment) :
    FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        when (position) {
            0 -> return ProfilePamsimasFragment()
            1 -> return LatarBelakangPamsimasFragment()
            2 -> return AsetPamsimasFragment()
        }
        return Fragment()
    }

}