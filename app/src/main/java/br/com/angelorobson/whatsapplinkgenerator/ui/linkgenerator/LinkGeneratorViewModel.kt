package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import br.com.angelorobson.whatsapplinkgenerator.model.domains.History
import br.com.angelorobson.whatsapplinkgenerator.model.repositories.CountryRepository
import br.com.angelorobson.whatsapplinkgenerator.model.repositories.HistoryRepository
import br.com.angelorobson.whatsapplinkgenerator.ui.MobiusVM
import br.com.angelorobson.whatsapplinkgenerator.ui.share.copyToClipBoard
import br.com.angelorobson.whatsapplinkgenerator.ui.share.getNow
import br.com.angelorobson.whatsapplinkgenerator.ui.share.sendMessageToWhatsApp
import br.com.angelorobson.whatsapplinkgenerator.ui.share.showDialogWithResString
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.ActivityService
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.HandlerErrorRemoteDataSource.validateStatusCode
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.IdlingResource
import br.com.angelorobson.whatsapplinkgenerator.ui.worker.ScheduleMessageWorker
import com.paulinasadowska.rxworkmanagerobservers.extensions.getWorkInfoByIdObservable
import com.spotify.mobius.Next
import com.spotify.mobius.Next.*
import com.spotify.mobius.Update
import com.spotify.mobius.rx2.RxMobius
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


fun linkGeneratorUpdate(
    model: LinkGeneratorModel,
    event: LinkGeneratorEvent
): Next<LinkGeneratorModel, LinkGeneratorEffect> {
    return when (event) {
        is InitialEvent -> dispatch(setOf(ObserveCountriesEffect))
        is CountriesLoadedEvent -> next(
            model.copy(
                linkGeneratorResult = LinkGeneratorResult.CountriesLoaded(
                    countries = event.countries,
                    isLoading = false
                )
            )
        )
        is CountriesExceptionEvent -> next(
            model.copy(
                linkGeneratorResult = LinkGeneratorResult.Error(
                    errorMessage = event.errorMessage,
                    isLoading = false
                )
            )
        )
        is ButtonSendClickedEvent ->
            if (event.isFormValid) {
                dispatch<LinkGeneratorModel, LinkGeneratorEffect>(
                    setOf(
                        SaveHistoryEffect(
                            history = History(
                                createdAt = getNow(),
                                country = event.country,
                                message = event.message,
                                phoneNumber = event.phoneNumber
                            )
                        )
                    )
                )
            } else {
                noChange()
            }
        is ButtonCopyClickedEvent ->
            if (event.isFormValid) {
                dispatch<LinkGeneratorModel, LinkGeneratorEffect>(
                    setOf(
                        CopyToClipBoardEffect(
                            countryCode = event.countryCode,
                            phoneNumber = event.phoneNumber,
                            message = event.message
                        )
                    )
                )
            } else {
                noChange()
            }
        is SendMessageToWhatsAppEvent -> dispatch(setOf(SendMessageToWhatsAppEffect(event.history)))
        is ScheduleMessageEvent -> dispatch(setOf(ScheduleMessageToWhatsAppEffect(
            event.country,
            event.phoneNumber,
            event.message,
            event.delay)
        ))


    }
}

class LinkGeneratorViewModel @Inject constructor(
    repository: CountryRepository,
    historyRepository: HistoryRepository,
    idlingResource: IdlingResource,
    activityService: ActivityService
) : MobiusVM<LinkGeneratorModel, LinkGeneratorEvent, LinkGeneratorEffect>(
    "LinkGeneratorViewModel",
    Update(::linkGeneratorUpdate),
    LinkGeneratorModel(),
    RxMobius.subtypeEffectHandler<LinkGeneratorEffect, LinkGeneratorEvent>()
        .addTransformer(ObserveCountriesEffect::class.java) { upstream ->
            upstream.switchMap {
                idlingResource.increment()
                repository.getAllFromApi()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map {
                        idlingResource.decrement()
                        CountriesLoadedEvent(it) as LinkGeneratorEvent
                    }
                    .onErrorReturn {
                        idlingResource.decrement()
                        val errorMessage = validateStatusCode(it)
                        showDialogWithResString(errorMessage.toInt(), activityService.activity)

                        CountriesExceptionEvent(errorMessage)
                    }
            }
        }
        .addTransformer(SaveHistoryEffect::class.java) { upstream ->
            upstream.switchMap { effect ->
                idlingResource.increment()
                historyRepository.saveHistory(effect.history)
                    .subscribeOn(Schedulers.newThread())
                    .doOnComplete {
                        idlingResource.decrement()
                    }
                    .toSingleDefault(SendMessageToWhatsAppEvent(effect.history) as LinkGeneratorEvent)
                    .toObservable()
                    .onErrorReturn {
                        idlingResource.decrement()
                        CountriesExceptionEvent(it.localizedMessage) as LinkGeneratorEvent
                    }

            }
        }
        .addConsumer(CopyToClipBoardEffect::class.java) { effect ->
            idlingResource.increment()

            copyToClipBoard(
                activityService.activity,
                effect.countryCode,
                effect.phoneNumber,
                effect.message
            )

            idlingResource.decrement()
        }.addConsumer(SendMessageToWhatsAppEffect::class.java) { effect ->
            idlingResource.increment()

            sendMessageToWhatsApp(
                activityService.activity,
                effect.history
            )

            idlingResource.decrement()
        }.addConsumer(ScheduleMessageToWhatsAppEffect::class.java) { effect ->
            val notificationWork = OneTimeWorkRequestBuilder<ScheduleMessageWorker>()
                .setInitialDelay(effect.delay, TimeUnit.SECONDS)
                .build()

            val instanceWorkManager = WorkManager
                .getInstance(activityService.activity)

            instanceWorkManager
                .beginUniqueWork("SCHEDULE_MESSAGE_WORK_ID", ExistingWorkPolicy.REPLACE, notificationWork)
                .enqueue()
            
            instanceWorkManager
                .getWorkInfoByIdObservable(notificationWork.id)
                .subscribe { workerInfo ->
                    if (workerInfo.state == WorkInfo.State.SUCCEEDED) {
                        val history = History(
                            createdAt = getNow(),
                            message = effect.message,
                            phoneNumber = effect.phoneNumber,
                            country = effect.country
                        )
                        sendMessageToWhatsApp(
                            activityService.activity,
                            history
                        )
                    }
                }
        }
        .build()

)