package br.com.angelorobson.whatsapplinkgenerator.ui.history

import br.com.angelorobson.whatsapplinkgenerator.model.repositories.CountryRepository
import br.com.angelorobson.whatsapplinkgenerator.model.repositories.HistoryRepository
import br.com.angelorobson.whatsapplinkgenerator.ui.MobiusVM
import br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator.LinkGeneratorEffect
import br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator.LinkGeneratorEvent
import br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator.LinkGeneratorModel
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.Navigator
import com.spotify.mobius.Next
import com.spotify.mobius.Next.*
import com.spotify.mobius.Update
import com.spotify.mobius.rx2.RxMobius
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

fun historyUpdate(
    model: HistoryModel,
    event: HistoryEvent
): Next<HistoryModel, HistoryEffect> {
    return when (event) {
        Initial -> dispatch(setOf(ObserverHistories))
        is HistoryLoaded -> next(
            model.copy(
                historyResult = HistoryResult.HistoryLoaded(
                    histories = event.histories
                )
            )
        )
        is HistoryException -> next(
            model.copy(
                historyResult = HistoryResult.Error(
                    errorMessage = event.errorMessage
                )
            )
        )
    }
}

class HistoryViewModel @Inject constructor(
    repository: HistoryRepository,
    navigator: Navigator
) : MobiusVM<HistoryModel, HistoryEvent, HistoryEffect>(
    "HistoryViewModel",
    Update(::historyUpdate),
    HistoryModel(),
    RxMobius.subtypeEffectHandler<HistoryEffect, HistoryEvent>()
        .addTransformer(ObserverHistories::class.java) { upstream ->
            upstream.switchMap {
                repository.getAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map {
                        HistoryLoaded(it)
                    }
                    .doOnError {
                        HistoryException(it.localizedMessage)
                    }
            }
        }.build()
)
