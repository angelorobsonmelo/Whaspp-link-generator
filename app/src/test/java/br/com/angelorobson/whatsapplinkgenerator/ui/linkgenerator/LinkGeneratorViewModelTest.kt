package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator

import br.com.angelorobson.whatsapplinkgenerator.model.builders.CountryBuild
import br.com.angelorobson.whatsapplinkgenerator.model.builders.HistoryBuild
import br.com.angelorobson.whatsapplinkgenerator.ui.share.getNow
import com.spotify.mobius.test.NextMatchers.*
import com.spotify.mobius.test.UpdateSpec
import com.spotify.mobius.test.UpdateSpec.assertThatNext
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Test

class LinkGeneratorViewModelTest {

    private lateinit var updateSpec: UpdateSpec<LinkGeneratorModel, LinkGeneratorEvent, LinkGeneratorEffect>

    private val dateTimeFormatted = "27/05/2020 12:00"
    private val message = "message"
    private val phoneNumber = "991228122"

    private val country = CountryBuild.Builder()
        .oneCountry()
        .build()

    private val history = HistoryBuild.Builder()
        .id(null)
        .createdAt(dateTimeFormatted)
        .country(country)
        .message(message)
        .phoneNumber(phoneNumber)
        .build()


    @Before
    fun setUp() {
        updateSpec = UpdateSpec(::linkGeneratorUpdate)
    }

    @Test
    fun initial_observerCountriesEffectDispatched() {
        val model = LinkGeneratorModel()
        updateSpec
            .given(model)
            .whenEvent(InitialEvent)
            .then(
                assertThatNext<LinkGeneratorModel, LinkGeneratorEffect>(
                    hasNoModel(),
                    hasEffects(ObserveCountriesEffect)
                )
            )
    }

    @Test
    fun countriesLoadedEvent_CountriesLoadedUpdated() {
        val model = LinkGeneratorModel()
        val countries = listOf(country, country, country)

        updateSpec
            .given(model)
            .whenEvent(CountriesLoadedEvent(countries = countries))
            .then(
                assertThatNext<LinkGeneratorModel, LinkGeneratorEffect>(
                    hasModel(
                        model.copy(
                            linkGeneratorResult = LinkGeneratorResult.CountriesLoaded(
                                countries = countries,
                                isLoading = false
                            )
                        )
                    ),
                    hasNoEffects()
                )
            )
    }

    @Test
    fun countriesExceptionEvent_getMessageError() {
        val model = LinkGeneratorModel()
        val errorMessage = "error"

        updateSpec
            .given(model)
            .whenEvent(
                CountriesExceptionEvent(
                    errorMessage = errorMessage
                )
            )
            .then(
                assertThatNext<LinkGeneratorModel, LinkGeneratorEffect>(
                    hasModel(
                        model.copy(
                            linkGeneratorResult = LinkGeneratorResult.Error(
                                errorMessage = errorMessage,
                                isLoading = false
                            )
                        )
                    ),
                    hasNoEffects()
                )
            )
    }

    @Test
    fun buttonSendClickedEvent_whenFormValid_saveHistoryEffectDispatched() {
        val model = LinkGeneratorModel()

        mockkStatic("br.com.angelorobson.whatsapplinkgenerator.ui.share.DateTimeUtilsKt")
        every { getNow() } returns dateTimeFormatted

        updateSpec
            .given(model)
            .whenEvent(
                ButtonSendClickedEvent(
                    countryCode = country.areaCode,
                    phoneNumber = phoneNumber,
                    message = message,
                    country = country,
                    isFormValid = true
                )
            )
            .then(
                assertThatNext<LinkGeneratorModel, LinkGeneratorEffect>(
                    hasEffects(
                        SaveHistoryEffect(
                            history = history
                        )
                    ),
                    hasNoModel()
                )
            )
    }


    @Test
    fun buttonSendClickedEvent_whenFormInValid_hasNoChange() {
        val model = LinkGeneratorModel()

        updateSpec
            .given(model)
            .whenEvent(
                ButtonSendClickedEvent(isFormValid = false)
            )
            .then(
                assertThatNext<LinkGeneratorModel, LinkGeneratorEffect>(
                    hasNothing()
                )
            )
    }

    @Test
    fun sendMessageToWhatsAppEvent_SendMessageToWhatsAppEffectDispatched() {
        val model = LinkGeneratorModel()

        updateSpec
            .given(model)
            .whenEvent(
                SendMessageToWhatsAppEvent(history = history)
            )
            .then(
                assertThatNext<LinkGeneratorModel, LinkGeneratorEffect>(
                    hasEffects(SendMessageToWhatsAppEffect(history = history)),
                    hasNoModel()
                )
            )
    }


}