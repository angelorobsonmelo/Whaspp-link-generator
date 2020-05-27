package br.com.angelorobson.whatsapplinkgenerator.ui.history.widgets

import android.app.Activity
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.databinding.HistoryRowBinding
import br.com.angelorobson.whatsapplinkgenerator.model.domains.History
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.extensions.DiffUtilCallback
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.phone_content.*


class HistoryAdapter(private val activity: Activity) :
    ListAdapter<History, HistoryViewHolder>(DiffUtilCallback<History>()) {

    private val historyClicksSubject = PublishSubject.create<Int>()
    val historyClicks: Observable<History> = historyClicksSubject
        .map { position -> getItem(position) }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            viewType,
            parent,
            false
        )

        val binding = DataBindingUtil.bind<HistoryRowBinding>(view)

        return HistoryViewHolder(
            view,
            binding,
            activity,
            historyClicksSubject
        )
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return R.layout.history_row
    }


}


class HistoryViewHolder(
    override val containerView: View,
    private val binding: HistoryRowBinding?,
    private val activity: Activity,
    private val historyClicksSubject: PublishSubject<Int>
) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(history: History) {
        binding?.apply {
            item = history
            val country = history.country
            val uri = Uri.parse(country.flag)

            GlideToVectorYou.justLoadImage(activity, uri, ivFlag)
            ivSend.clicks().map { adapterPosition }.subscribe(historyClicksSubject)
            executePendingBindings()
        }
    }
}