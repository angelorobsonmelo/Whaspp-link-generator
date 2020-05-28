package br.com.angelorobson.whatsapplinkgenerator.ui.history

import br.com.angelorobson.whatsapplinkgenerator.model.builders.HistoryBuild
import com.spotify.mobius.test.NextMatchers.*
import com.spotify.mobius.test.UpdateSpec
import org.junit.Before
import org.junit.Test

class HistoryViewModelTest {

    private lateinit var updateSpec: UpdateSpec<HistoryModel, HistoryEvent, HistoryEffect>

    private val history = HistoryBuild.Builder()
        .oneHistory()
        .build()

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