package br.com.angelorobson.whatsapplinkgenerator.ui.history

import br.com.angelorobson.whatsapplinkgenerator.model.domains.History

data class HistoryModel(
    val historyResult: HistoryResult = HistoryResult.Loading()
)

sealed class HistoryResult {
    data class Error(
        val errorMessage: String,
        val isLoading: Boolean = false
    ) : HistoryResult()

    data class Loading(val isLoading: Boolean = true) : HistoryResult()

    data class HistoryLoaded(
        val histories: List<History> = listOf(),
        val isLoading: Boolean = false
    ) : HistoryResult()


}