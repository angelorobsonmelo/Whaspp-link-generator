package br.com.angelorobson.whatsapplinkgenerator.ui.share

import android.app.Activity
import android.content.Intent
import android.net.Uri
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.model.domains.History
import java.text.MessageFormat

const val link = "https://wa.me/{0}{1}?text={2}"

fun sendMessageToWhatsApp(
    activity: Activity,
    history: History
) {
    try {
        val packageManager = activity.packageManager
        val i = Intent(Intent.ACTION_VIEW)
        val url =
            MessageFormat.format(
                link,
                history.country.areaCode,
                history.phoneNumber,
                history.message
            )

        i.setPackage("com.whatsapp")
        i.data = Uri.parse(url)
        if (i.resolveActivity(packageManager) != null) {
            activity.startActivity(i)
        } else {
            showToast(activity.getString(R.string.whatApp_not_installed), activity)
        }
    } catch (e: Exception) {
        showToast(activity.getString(R.string.whatApp_not_installed), activity)
    }
}