package br.com.angelorobson.whatsapplinkgenerator.ui.history

import androidx.fragment.app.Fragment
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.ui.getViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable


class HistoryFragment : Fragment(R.layout.fragment_history) {

    private lateinit var disposable: Disposable

    override fun onStart() {
        super.onStart()

        disposable = Observable.empty<HistoryEvent>()
            .compose(getViewModel(HistoryViewModel::class).init(Initial))
            .subscribe { model ->
                if (model.historyResult is HistoryResult.Loading) {
                    println()
                }
                if (model.historyResult is HistoryResult.HistoryLoaded) {
                    println()
                }
                if (model.historyResult is HistoryResult.Error) {
                    println()
                }

            }
    }

}
