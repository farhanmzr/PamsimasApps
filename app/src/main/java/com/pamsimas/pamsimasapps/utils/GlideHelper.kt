package com.pamsimas.pamsimasapps.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.pamsimas.pamsimasapps.R

fun ImageView.loadImage(url: String?) {
    Glide.with(this)
        .load(url)
        .apply(
            RequestOptions.placeholderOf(R.drawable.ic_loading)
                .error(R.drawable.ic_error))
        .into(this)
}