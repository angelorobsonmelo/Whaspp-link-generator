package br.com.angelorobson.whatsapplinkgenerator.ui.history

import br.com.angelorobson.whatsapplinkgenerator.model.domains.History

sealed class HistoryEvent

object InitialEvent : HistoryEvent()

data class HistoryLoadedEvent(
    val histories: List<History>
) : HistoryEvent()

data class ResendMessageToWhatsAppEvent(
    val history: History
) : HistoryEvent()

data class HistoryExceptionEvent(val errorMessage: String) : HistoryEvent()
