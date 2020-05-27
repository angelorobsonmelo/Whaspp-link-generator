package br.com.angelorobson.whatsapplinkgenerator.ui.history

import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.ui.getViewModel
import br.com.angelorobson.whatsapplinkgenerator.ui.history.widgets.HistoryAdapter
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_history.*


class HistoryFragment : Fragment(R.layout.fragment_history) {

    private val compositeDisposable = CompositeDisposable()

    override fun onStart() {
        super.onStart()

        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = HistoryAdapter(requireActivity())
        recyclerView.adapter = adapter

        val disposable =
            Observable.mergeArray(adapter.historyClicks.map { ResendMessageToWhatsAppEvent(it) }
            )
                .compose(getViewModel(HistoryViewModel::class).init(InitialEvent))
                .subscribe { model ->
                    if (model.historyResult is HistoryResult.Loading) {
                        tvEmpty.isVisible = false
                        progress_horizontal.isVisible = model.historyResult.isLoading
                    }
                    if (model.historyResult is HistoryResult.HistoryLoaded) {
                        val histories = model.historyResult.histories

                        progress_horizontal.isVisible = model.historyResult.isLoading

                        if (histories.isEmpty()) {
                            tvEmpty.isVisible = true
                            return@subscribe
                        }

                        adapter.submitList(histories)
                    }
                    if (model.historyResult is HistoryResult.Error) {
                        tvEmpty.isVisible = false
                        progress_horizontal.isVisible = model.historyResult.isLoading
                    }

                }

        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

}
