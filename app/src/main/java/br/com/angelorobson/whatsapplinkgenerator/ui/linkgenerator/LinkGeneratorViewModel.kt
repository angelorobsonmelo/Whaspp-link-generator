package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator

import br.com.angelorobson.whatsapplinkgenerator.model.domains.History
import br.com.angelorobson.whatsapplinkgenerator.ui.MobiusVM
import br.com.angelorobson.whatsapplinkgenerator.model.repositories.CountryRepository
import br.com.angelorobson.whatsapplinkgenerator.model.repositories.HistoryRepository
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.HandlerErrorRemoteDataSource.validateStatusCode
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.Navigator
import com.spotify.mobius.Next
import com.spotify.mobius.Next.*
import com.spotify.mobius.Update
import com.spotify.mobius.rx2.RxMobius
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


fun linkGeneratorUpdate(
    model: LinkGeneratorModel,
    event: LinkGeneratorEvent
): Next<LinkGeneratorModel, LinkGeneratorEffect> {
    return when (event) {
        is Initial -> dispatch(setOf(ObserveCountries))
        is CountriesLoaded -> next(
            model.copy(
                linkGeneratorResult = LinkGeneratorResult.CountriesLoaded(
                    countries = event.countries,
                    isLoading = false
                )
            )
        )
        is CountriesException -> next(
            model.copy(
                linkGeneratorResult = LinkGeneratorResult.Error(
                    errorMessage = event.errorMessage,
                    isLoading = false
                )
            )
        )
        FormInvalid -> noChange()
        is ButtonSendClicked -> next(
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
        )
    }
}

class LinkGeneratorViewModel @Inject constructor(
    repository: CountryRepository,
    historyRepository: HistoryRepository,
    navigator: Navigator
) : MobiusVM<LinkGeneratorModel, LinkGeneratorEvent, LinkGeneratorEffect>(
    "LinkGeneratorViewModel",
    Update(::linkGeneratorUpdate),
    LinkGeneratorModel(),
    RxMobius.subtypeEffectHandler<LinkGeneratorEffect, LinkGeneratorEvent>()
        .addTransformer(ObserveCountries::class.java) { upstream ->
            upstream.switchMap {
                repository.getAllFromApi()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map {
                        CountriesLoaded(it)
                    }
                    .doOnError {
                        val errorMessage = validateStatusCode(it)
                        CountriesException(errorMessage)
                    }
            }
        }
        .addTransformer(SaveHistory::class.java) { upstream ->
            upstream.switchMap { effect ->
                historyRepository.saveHistory(effect.history)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .toObservable<LinkGeneratorEvent>()
                    .doOnError {
                        CountriesException(it.localizedMessage)
                    }
            }
        }
        .build()

)

fun getNow(): String {
    return LocalDateTime.now().toString()
}
