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


private const val link = "https://api.whatsapp.com/send?phone={0}{1}&text={2}"
const val WHAT_APP_RESULT_CODE = 985

var countStartActivity = 1

fun sendMessageToWhatsApp(
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
            i.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.addFlags(Intent.FLAG_FROM_BACKGROUND);

            if (countStartActivity == 0) {
                countStartActivity++
                return
            }
            activity.startActivity(i)
            countStartActivity = 0

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
}

fun copyToClipBoard(
    info: LinkGeneratorResult.ContactInformationToCopy,
    activity: Activity?
) {
    val url = MessageFormat.format(link, info.countryCode, info.phoneNumber, info.message)
    val clipboard =
        activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("linkWhatsApp", url)

    clipboard.setPrimaryClip(clip)
    showToast(
        activity.getString(
            R.string.copied
        ), activity
    )
}

fun showToast(message: String, context: Context, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(
        context,
        message,
        duration
    ).show()
}