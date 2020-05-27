package br.com.angelorobson.whatsapplinkgenerator.ui.history

import br.com.angelorobson.whatsapplinkgenerator.model.domains.History

sealed class HistoryEffect

object ObserverHistoriesEffect : HistoryEffect()

data class ResendMessageToWhatsAppEffect(
    val history: History
) : HistoryEffect()