package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import br.com.angelorobson.whatsapplinkgenerator.R
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
        /*  is ButtonSendClicked -> next(
              model.copy(
                  linkGeneratorResult = LinkGeneratorResult.ContactInformationToSend(
                      countryCode = event.countryCode,
                      phoneNumber = event.phoneNumber,
                      message = event.message
                  )
              ),
              setOf(
                  SaveHistory(
                      History(
                          createdAt = getNow(),
                          country = event.country,
                          message = event.message,
                          phoneNumber = event.phoneNumber
                      )
                  )
              )
          )
          is ButtonCopyClicked -> next(
              model.copy(
                  linkGeneratorResult = LinkGeneratorResult.ContactInformationToCopy(
                      countryCode = event.countryCode,
                      phoneNumber = event.phoneNumber,
                      message = event.message
                  )
              )
          )*/
        is ButtonSendClickedEvent -> noChange()
        is ButtonCopyClickedEvent -> dispatch(
            setOf(
                CopyToClipBoardEffect(
                    countryCode = event.countryCode,
                    phoneNumber = event.phoneNumber,
                    message = event.message
                )
            )
        )
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
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toObservable<LinkGeneratorEvent>()
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
        }

        .build()

)

/*private fun isFormValid(): Boolean {
    var valid = true
    if (etRegionCode.text?.isEmpty()!!) {
        etPhoneNumber.error = getString(R.string.empty_field)
        valid = false
    }

    if (etPhoneNumber.text?.isEmpty()!!) {
        etPhoneNumber.error = getString(R.string.empty_field)
        valid = false
    }

    if (etTextMessage.text?.isEmpty()!! || etTextMessage.text?.isBlank()!!) {
        etTextMessage.error = getString(R.string.empty_field)
        valid = false
    }

    return valid
}*/

fun getNow(): String {
    return LocalDateTime.now().toString()
}

fun copyToClipBoard(
    activity: android.app.Activity,
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

fun showToast(message: String, context: Context, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(
        context,
        message,
        duration
    ).show()
}
