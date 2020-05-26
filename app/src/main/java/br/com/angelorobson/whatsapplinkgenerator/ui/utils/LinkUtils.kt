package br.com.angelorobson.whatsapplinkgenerator.ui.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator.LinkGeneratorResult
import java.text.MessageFormat


private const val link = "https://wa.me/{0}{1}?text={2}"

/*fun sendMessageToWhatsApp(
    info: LinkGeneratorResult.ContactInformationToSend,
    activity: Activity
) {
    try {
        val packageManager = activity.packageManager
        val i = Intent(Intent.ACTION_VIEW)
        val url = MessageFormat.format(link, info.countryCode, info.phoneNumber, info.message)

        i.setPackage("com.whatsapp")
        i.data = Uri.parse(url)
        if (i.resolveActivity(packageManager) != null) {
            activity.startActivity(i)
        } else {
            showToast(
                activity.getString(
                    R.string.whatApp_not_installed
                ), activity
            )
        }
    } catch (e: Exception) {
        showToast(
            activity.getString(
                R.string.whatApp_not_installed
            ), activity
        )
    }
}*/
