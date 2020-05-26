package br.com.angelorobson.whatsapplinkgenerator.ui.utils

import android.view.View
import android.view.View.*
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.extensions.convertDateToString
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.extensions.convertDateToStringDDMMM
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.extensions.formatToServerDateTimeDefaults
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.extensions.formatToViewDateTimeDefaults
import java.util.*


@BindingAdapter("date")
fun convertDateToString(textView: TextView, date: Date) {
    textView.text = date.convertDateToString()
}

@BindingAdapter("dateTime")
fun convertdateTimeToString(textView: TextView, date: Date) {
    textView.text = date.formatToServerDateTimeDefaults()
}

@BindingAdapter("initialDate")
fun convertInitialToString(textView: TextView, initialDate: Date) {
    textView.text = initialDate.convertDateToStringDDMMM()
}

@BindingAdapter("convertFormatToViewDateTimeDefaults")
fun convertFormatToViewDateTimeDefaults(textView: TextView, date: Date) {
    textView.text = date.formatToViewDateTimeDefaults()
}

@BindingAdapter("finalDate")
fun convertFinalDateToString(textView: TextView, finalDate: Date) {

    textView.text = finalDate.convertDateToStringDDMMM()
}


/*@BindingAdapter("bind:areaCode", "bind:phoneNumber")
fun areaCodeAndNumber(
    areaCode: String?,
    phoneNumber: String?,
    textView: TextView
) {
    textView.text = textView.context.getString(R.string.area_code_number, areaCode, phoneNumber)

}*/

@BindingAdapter("areaCode")
fun areaCode(
    textView: TextView,
    areaCode: String?
) {
    textView.text = textView.context.getString(R.string.area_code, areaCode)
}

@BindingAdapter("visibleOrGone")
fun View.setVisibleOrGone(show: Boolean) {
    visibility = if (show) VISIBLE else GONE
}

@BindingAdapter("visible")
fun View.setVisible(show: Boolean) {
    visibility = if (show) VISIBLE else INVISIBLE
}