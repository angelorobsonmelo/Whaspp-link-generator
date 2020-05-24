package br.com.angelorobson.whatsapplinkgenerator.ui.history.widgets

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.model.domains.History
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import kotlinx.android.extensions.LayoutContainer


class HistoryAdapter(private val activity: Activity) :
    androidx.recyclerview.widget.ListAdapter<History, HistoryViewHolder>(DiffUtilCallback()) {


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

private class DiffUtilCallback : DiffUtil.ItemCallback<History>() {

    override fun areItemsTheSame(oldItem: History, newItem: History): Boolean = oldItem == newItem

    override fun areContentsTheSame(oldItem: History, newItem: History): Boolean =
        oldItem == newItem
}

class HistoryViewHolder(
    override val containerView: View,
    private val activity: Activity
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(history: History) {
        with(itemView) {
            val country = history.country
            val context = containerView.context

            val imageFlag = findViewById<ImageView>(R.id.ivFlag)
            val textViewCountryName = findViewById<TextView>(R.id.tvCountryName)
            val textViewCodeArea = findViewById<TextView>(R.id.tvCountryCode)
            val textViewDate = findViewById<TextView>(R.id.tvDate)
            val textViewMessage = findViewById<TextView>(R.id.tvMessage)

            val uri =
                Uri.parse(country.flag)

            GlideToVectorYou.justLoadImage(activity, uri, imageFlag!!)

            textViewDate.text = history.createdAt
            textViewMessage.text = history.message

            textViewCountryName?.text = country.countryShortName
            textViewCodeArea?.text = context.getString(R.string.area_code_number, country.areaCode, history.phoneNumber)
        }

    }
}