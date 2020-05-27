package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.model.domains.History
import br.com.angelorobson.whatsapplinkgenerator.model.repositories.CountryRepository
import br.com.angelorobson.whatsapplinkgenerator.model.repositories.HistoryRepository
import br.com.angelorobson.whatsapplinkgenerator.ui.MobiusVM
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.ActivityService
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.HandlerErrorRemoteDataSource.validateStatusCode
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.Navigator
import com.spotify.mobius.Next
import com.spotify.mobius.Next.*
import com.spotify.mobius.Update
import com.spotify.mobius.rx2.RxMobius
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDateTime
import java.text.MessageFormat
import javax.inject.Inject

private const val link = "https://wa.me/{0}{1}?text={2}"

fun linkGeneratorUpdate(
    model: LinkGeneratorModel,
    event: LinkGeneratorEvent
): Next<LinkGeneratorModel, LinkGeneratorEffect> {
    return when (event) {
        is Initial -> dispatch(setOf(ObserveCountriesEffect))
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
        FormInvalidEvent -> noChange()
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
    }
}

class LinkGeneratorViewModel @Inject constructor(
    repository: CountryRepository,
    historyRepository: HistoryRepository,
    navigator: Navigator,
    activityService: ActivityService
) : MobiusVM<LinkGeneratorModel, LinkGeneratorEvent, LinkGeneratorEffect>(
    "LinkGeneratorViewModel",
    Update(::linkGeneratorUpdate),
    LinkGeneratorModel(),
    RxMobius.subtypeEffectHandler<LinkGeneratorEffect, LinkGeneratorEvent>()
        .addTransformer(ObserveCountriesEffect::class.java) { upstream ->
            upstream.switchMap {
                repository.getAllFromApi()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map {
                        CountriesLoadedEvent(it)
                    }
                    .doOnError {
                        val errorMessage = validateStatusCode(it)
                        showToast(errorMessage, activityService.activity, Toast.LENGTH_LONG)
                        CountriesExceptionEvent(errorMessage)
                    }
            }
        }
        .addTransformer(SaveHistoryEffect::class.java) { upstream ->
            upstream.switchMap { effect ->
                historyRepository.saveHistory(effect.history)
                    .subscribeOn(Schedulers.newThread())
                    .toSingleDefault(SendMessageToWhatsAppEvent(effect.history))
                    .toObservable()
                    .doOnError {
                        CountriesExceptionEvent(it.localizedMessage)
                    }

            }
        }
        .addTransformer(CopyToClipBoardEffect::class.java) { upstream ->
            upstream.switchMap { effect ->
                copyToClipBoard(
                    activityService.activity,
                    effect.countryCode,
                    effect.phoneNumber,
                    effect.message
                ).toObservable<LinkGeneratorEvent>()
                    .doOnError {
                        CountriesExceptionEvent(it.localizedMessage)
                    }
            }
        }.addTransformer(SendMessageToWhatsAppEffect::class.java) { upstream ->
            upstream.switchMap { effect ->
                sendMessageToWhatsApp(
                    activityService.activity,
                    effect.history
                ).toObservable<LinkGeneratorEvent>()
                    .doOnError {
                        CountriesExceptionEvent(it.localizedMessage)
                    }
            }
        }
        .build()

)

fun getNow(): String {
    return LocalDateTime.now().toString()
}

fun copyToClipBoard(
    activity: Activity,
    countryCode: String,
    phoneNumber: String,
    message: String
): Completable {
    return Single.fromCallable {}
        .flatMapCompletable {
            val url = MessageFormat.format(link, countryCode, phoneNumber, message)
            val clipboard =
                activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip: ClipData = ClipData.newPlainText("linkWhatsApp", url)

            clipboard.setPrimaryClip(clip)
            CompletableSource {
                it.onComplete()
                showToast(activity.getString(R.string.copied), activity.applicationContext)
            }
        }
}

fun sendMessageToWhatsApp(
    activity: Activity,
    history: History
): Completable {
    return Single.fromCallable { history }
        .flatMapCompletable {
            try {
                val packageManager = activity.packageManager
                val i = Intent(Intent.ACTION_VIEW)
                val url =
                    MessageFormat.format(link, it.country.areaCode, it.phoneNumber, it.message)

                i.setPackage("com.whatsapp")
                i.data = Uri.parse(url)
                if (i.resolveActivity(packageManager) != null) {
                    activity.startActivity(i)
                } else {
                    showToast(activity.getString(R.string.whatApp_not_installed), activity)
                }
            } catch (e: Exception) {
                showToast(activity.getString(R.string.whatApp_not_installed), activity)
            }
            CompletableSource { completableObserver ->
                completableObserver.onComplete()
            }
        }
}


fun showToast(message: String, context: Context, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(
        context,
        message,
        duration
    ).show()
}
