package com.pamsimas.pamsimasapps.ui.dashboard.search

import com.pamsimas.pamsimasapps.models.Customers

interface SearchClickCallback{
    fun onItemClicked(data: Customers)
}