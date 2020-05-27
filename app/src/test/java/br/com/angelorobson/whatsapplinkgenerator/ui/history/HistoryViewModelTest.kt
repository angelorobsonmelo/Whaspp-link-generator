package br.com.angelorobson.whatsapplinkgenerator.ui.history

import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import br.com.angelorobson.whatsapplinkgenerator.model.domains.History
import com.spotify.mobius.test.NextMatchers.*
import com.spotify.mobius.test.UpdateSpec
import org.junit.Before
import org.junit.Test

class HistoryViewModelTest {

    private lateinit var updateSpec: UpdateSpec<HistoryModel, HistoryEvent, HistoryEffect>

    private val dateTime = "27/05/2020 12:00"
    private val message = "message"
    private val phoneNumber = "phonenumber"

    private val country =
        Country("Brazil", "flag", areaCode = "areacode", countryShortName = "country")

    private val history = History(
        createdAt = dateTime,
        country = country,
        message = message,
        phoneNumber = phoneNumber
    )

    @Before
    fun setUp() {
        updateSpec = UpdateSpec(::historyUpdate)
    }

    @Test
    fun initialEvent_ObserverHistoriesEffectDispatched() {
        val model = HistoryModel()

        updateSpec
            .given(model)
            .whenEvent(InitialEvent)
            .then(
                UpdateSpec.assertThatNext<HistoryModel, HistoryEffect>(
                    hasEffects(ObserverHistoriesEffect),
                    hasNoModel()
                )
            )

    }

    @Test
    fun historyLoadedEvent_historyResultLoadedUpdated() {
        val model = HistoryModel()
        val histories = listOf(history, history, history)

        updateSpec
            .given(model)
            .whenEvent(
                HistoryLoadedEvent(
                    histories = listOf(history, history, history)
                )
            )
            .then(
                UpdateSpec.assertThatNext<HistoryModel, HistoryEffect>(
                    hasModel(
                        model.copy(
                            historyResult = HistoryResult.HistoryLoaded(
                                histories = histories
                            )
                        )
                    ),
                    hasNoEffects()
                )
            )

    }

    @Test
    fun historyExceptionEvent_getMessageError() {
        val model = HistoryModel()
        val errorMessage = "error"

        updateSpec
            .given(model)
            .whenEvent(
                HistoryExceptionEvent(
                    errorMessage = errorMessage
                )
            )
            .then(
                UpdateSpec.assertThatNext<HistoryModel, HistoryEffect>(
                    hasModel(
                        model.copy(
                            historyResult = HistoryResult.Error(
                                errorMessage = errorMessage
                            )
                        )
                    ),
                    hasNoEffects()
                )
            )
    }

    @Test
    fun resendMessageToWhatsAppEvent_resendMessageToWhatsAppEffectDispatched() {
        val model = HistoryModel()

        updateSpec
            .given(model)
            .whenEvent(
                ResendMessageToWhatsAppEvent(
                    history = history
                )
            )
            .then(
                UpdateSpec.assertThatNext<HistoryModel, HistoryEffect>(
                    hasEffects(
                        ResendMessageToWhatsAppEffect(
                            history = history
                        )
                    ),
                    hasNoModel()
                )
            )
    }

}