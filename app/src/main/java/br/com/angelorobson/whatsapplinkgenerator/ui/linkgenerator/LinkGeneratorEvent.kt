package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator

import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import br.com.angelorobson.whatsapplinkgenerator.model.domains.History
import br.com.ilhasoft.support.validation.Validator

sealed class LinkGeneratorEvent

object Initial : LinkGeneratorEvent()


data class ButtonSendClickedEvent(
    val countryCode: String = "",
    val phoneNumber: String = "",
    val message: String = "",
    val country: Country = Country(),
    val isFormValid: Boolean
) : LinkGeneratorEvent()

data class ButtonCopyClickedEvent(
    val countryCode: String = "",
    val phoneNumber: String = "",
    val message: String = "",
    val isFormValid: Boolean
) : LinkGeneratorEvent()

data class SendMessageToWhatsAppEvent(
    val history: History
) : LinkGeneratorEvent()

data class CountriesLoadedEvent(val countries: List<Country>) : LinkGeneratorEvent()

data class CountriesExceptionEvent(val errorMessage: String) : LinkGeneratorEvent()

object FormInvalidEvent : LinkGeneratorEvent()