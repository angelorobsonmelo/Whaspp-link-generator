package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator

import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import br.com.angelorobson.whatsapplinkgenerator.model.domains.History

sealed class LinkGeneratorEffect

object ObserveCountriesEffect : LinkGeneratorEffect()

data class SaveHistoryEffect(
    val history: History
) : LinkGeneratorEffect()

data class CopyToClipBoardEffect(
    val countryCode: String = "",
    val phoneNumber: String = "",
    val message: String = ""
) : LinkGeneratorEffect()

data class SendMessageToWhatsAppEffect(
    val history: History
) : LinkGeneratorEffect()

data class ScheduleMessageToWhatsAppEffect(
    val country: Country = Country(),
    val phoneNumber: String = "",
    val message: String = "",
    val delay: Long = 0
) : LinkGeneratorEffect()

