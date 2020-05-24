package br.com.angelorobson.whatsapplinkgenerator.ui.history

import br.com.angelorobson.whatsapplinkgenerator.model.domains.History

sealed class HistoryEvent

object Initial : HistoryEvent()

data class HistoryLoaded(
    val histories: List<History>
) : HistoryEvent()

data class HistoryException(val errorMessage: String) : HistoryEvent()
