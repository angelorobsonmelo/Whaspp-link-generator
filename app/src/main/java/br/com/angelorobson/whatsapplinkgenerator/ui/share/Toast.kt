package br.com.angelorobson.whatsapplinkgenerator.ui.share

import android.content.Context
import android.widget.Toast

fun showToast(message: String, context: Context, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(
        context,
        message,
        duration
    ).show()
}