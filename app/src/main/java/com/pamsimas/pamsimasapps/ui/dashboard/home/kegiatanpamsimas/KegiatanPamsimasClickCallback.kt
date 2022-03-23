package com.pamsimas.pamsimasapps.ui.dashboard.home.kegiatanpamsimas

import com.pamsimas.pamsimasapps.models.Kegiatan

interface KegiatanPamsimasClickCallback {
    fun onItemClicked(data: Kegiatan)
}