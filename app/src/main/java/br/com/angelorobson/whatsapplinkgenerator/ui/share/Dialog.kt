package br.com.angelorobson.whatsapplinkgenerator.ui.share

import android.content.Context
import androidx.appcompat.app.AlertDialog

fun showDialogWithResString(message: Int, context: Context) {
    val builder = AlertDialog.Builder(context)
    builder.apply {
        setMessage(message)
        setPositiveButton(
            android.R.string.ok
        ) { dialog, id ->
            dialog.dismiss()
        }
    }
    builder.create()
    builder.show()
}