package br.com.angelorobson.whatsapplinkgenerator.ui.history.widgets

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.model.domains.History
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.history_row.*
import kotlinx.android.synthetic.main.phone_content.*
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import java.util.*


class HistoryAdapter(private val activity: Activity) :
    androidx.recyclerview.widget.ListAdapter<History, HistoryViewHolder>(DiffUtilCallback<History>()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                viewType,
                parent,
                false
            ),
            activity
        )
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.history_row
    }


}

class DiffUtilCallback<T> : DiffUtil.ItemCallback<T>() {

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean =
        oldItem == newItem
}

class HistoryViewHolder(
    override val containerView: View,
    private val activity: Activity
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(history: History) {
        with(itemView) {
            val country = history.country

            val uri = Uri.parse(country.flag)
            val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            var dateTimeFormatted = ""

            val datetime = LocalDateTime.parse(history.createdAt)
            dateTimeFormatted = datetime.format(formatter)

            GlideToVectorYou.justLoadImage(activity, uri, ivFlag)

            if (Locale.getDefault().country == "BR") {
                val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                dateTimeFormatted = datetime.format(formatter)
            }

            tvDate.text = dateTimeFormatted

            tvMessage.text = history.message

            tvCountryName.text = country.countryShortName
            tvCountryCode.text =
                context.getString(R.string.area_code_number, country.areaCode, history.phoneNumber)
        }

    }
}