package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator

import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country

sealed class LinkGeneratorEvent

object Initial : LinkGeneratorEvent()


data class ButtonSendClickedEvent(
    val countryCode: String = "",
    val phoneNumber: String = "",
    val message: String = "",
    val country: Country = Country()
) : LinkGeneratorEvent()

data class ButtonCopyClickedEvent(
    val countryCode: String = "",
    val phoneNumber: String = "",
    val message: String = ""
) : LinkGeneratorEvent()

data class CountriesLoadedEvent(val countries: List<Country>) : LinkGeneratorEvent()

data class CountriesExceptionEvent(val errorMessage: String) : LinkGeneratorEvent()

object FormInvalidEvent : LinkGeneratorEvent()