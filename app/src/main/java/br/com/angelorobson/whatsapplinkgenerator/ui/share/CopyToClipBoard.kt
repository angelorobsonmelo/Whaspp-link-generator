package br.com.angelorobson.whatsapplinkgenerator.ui.share

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import br.com.angelorobson.whatsapplinkgenerator.R
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Single
import java.text.MessageFormat

fun copyToClipBoard(
    activity: Activity,
    countryCode: String,
    phoneNumber: String,
    message: String
) {
    val url = MessageFormat.format(link, countryCode, phoneNumber, message)
    val clipboard =
        activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip: ClipData = ClipData.newPlainText("linkWhatsApp", url)

    clipboard.setPrimaryClip(clip)
    showToast(activity.getString(R.string.copied), activity.applicationContext)
}