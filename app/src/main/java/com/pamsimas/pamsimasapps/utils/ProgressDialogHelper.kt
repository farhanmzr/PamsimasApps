package com.pamsimas.pamsimasapps.utils

import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.pamsimas.pamsimasapps.R

class ProgressDialogHelper {
    companion object {
        fun progressDialog(context: Context): Dialog {
            val dialog = Dialog(context)
            val inflate = LayoutInflater.from(context).inflate(R.layout.item_progress_dialog, null)
            dialog.setContentView(inflate)
            dialog.setCancelable(false)
            return dialog
        }
    }
}