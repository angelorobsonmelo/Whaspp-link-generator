package br.com.angelorobson.whatsapplinkgenerator.linkgenerator

import br.com.angelorobson.whatsapplinkgenerator.MobiusVM
import br.com.angelorobson.whatsapplinkgenerator.model.repositories.CountryRepository
import br.com.angelorobson.whatsapplinkgenerator.utils.Navigator
import com.spotify.mobius.Next
import com.spotify.mobius.Next.*
import com.spotify.mobius.Update
import com.spotify.mobius.rx2.RxMobius
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


fun linkGeneratorUpdate(
    model: LinkGeneratorModel,
    event: LinkGeneratorEvent
): Next<LinkGeneratorModel, LinkGeneratorEffect> {
    return when (event) {
        is Initial -> dispatch(setOf(ObserveCountries))
        is CountriesLoaded -> next(
            model.copy(countries = event.countries)
        )
        ButtonSendClicked -> noChange()
    }
}

class LinkGeneratorViewModel @Inject constructor(
    repository: CountryRepository,
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
            }
        }.build()

)