package br.com.angelorobson.whatsapplinkgenerator.ui.utils

import android.view.View
import android.view.View.*
import android.widget.TextView
import androidx.databinding.BindingAdapter
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.extensions.convertDateToString
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.extensions.convertDateToStringDDMMM
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.extensions.formatToServerDateTimeDefaults
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.extensions.formatToViewDateTimeDefaults
import kotlinx.android.synthetic.main.history_row.*
import kotlinx.android.synthetic.main.phone_content.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*

private const val BR = "BR"


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

@BindingAdapter("convertDateTimeToString")
fun convertDateTimeToString(textView: TextView, dateTimeString: String?) {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    var dateTimeFormatted = ""

    val datetime = LocalDateTime.parse(dateTimeString)
    dateTimeFormatted = datetime.format(formatter)

    if (Locale.getDefault().country == BR) {
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        dateTimeFormatted = datetime.format(formatter)
    }

    textView.text = dateTimeFormatted
}

@BindingAdapter("finalDate")
fun convertFinalDateToString(textView: TextView, finalDate: Date) {
    textView.text = finalDate.convertDateToStringDDMMM()
}

@BindingAdapter(value = ["areaCode", "phoneNumber"])
fun countryCodeAndPhoneNumber(textView: TextView, areaCode: String?, phoneNumber: String?) {
    textView.text = textView.context.getString(R.string.area_code_number, areaCode, phoneNumber)
}

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