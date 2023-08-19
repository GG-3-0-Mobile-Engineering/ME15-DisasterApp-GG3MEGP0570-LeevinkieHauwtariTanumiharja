package com.gigih.disastermap.utils

import android.view.View
import com.google.android.material.snackbar.Snackbar

class MessageHelper {
    companion object {
        fun showSnackbar(view: View, message: String) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()
        }

        fun showSnackbarWithAction(view: View, message: String, actionText: String, actionListener: View.OnClickListener) {
            Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction(actionText, actionListener)
                .show()
        }
    }
}