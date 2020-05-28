package br.com.angelorobson.whatsapplinkgenerator.ui.history

import br.com.angelorobson.whatsapplinkgenerator.model.repositories.HistoryRepository
import br.com.angelorobson.whatsapplinkgenerator.ui.MobiusVM
import br.com.angelorobson.whatsapplinkgenerator.ui.share.sendMessageToWhatsApp
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.ActivityService
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.IdlingResource
import com.spotify.mobius.Next
import com.spotify.mobius.Next.dispatch
import com.spotify.mobius.Next.next
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
        InitialEvent -> dispatch(setOf(ObserverHistoriesEffect))
        is HistoryLoadedEvent -> next(
            model.copy(
                historyResult = HistoryResult.HistoryLoaded(
                    histories = event.histories
                )
            )
        )
        is HistoryExceptionEvent -> next(
            model.copy(
                historyResult = HistoryResult.Error(
                    errorMessage = event.errorMessage
                )
            )
        )
        is ResendMessageToWhatsAppEvent -> dispatch(setOf(ResendMessageToWhatsAppEffect(event.history)))
    }
}

class HistoryViewModel @Inject constructor(
    repository: HistoryRepository,
    idlingResource: IdlingResource,
    activityService: ActivityService
) : MobiusVM<HistoryModel, HistoryEvent, HistoryEffect>(
    "HistoryViewModel",
    Update(::historyUpdate),
    HistoryModel(),
    RxMobius.subtypeEffectHandler<HistoryEffect, HistoryEvent>()
        .addTransformer(ObserverHistoriesEffect::class.java) { upstream ->
            upstream.switchMap {
                idlingResource.increment()
                repository.getAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map { histories ->
                        idlingResource.decrement()
                        HistoryLoadedEvent(histories) as HistoryEvent
                    }
                    .onErrorReturn {
                        HistoryExceptionEvent(it.localizedMessage) as HistoryEvent
                    }
            }
        }
        .addConsumer(ResendMessageToWhatsAppEffect::class.java) { effect ->
            idlingResource.increment()
            sendMessageToWhatsApp(activityService.activity, effect.history)
            idlingResource.decrement()
        }
        .build()
)
