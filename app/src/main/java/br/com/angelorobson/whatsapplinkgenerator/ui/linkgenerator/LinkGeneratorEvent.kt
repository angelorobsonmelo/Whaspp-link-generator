package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator

import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country

sealed class LinkGeneratorEvent

object Initial : LinkGeneratorEvent()


data class ButtonSendClicked(
    val countryCode: String = "",
    val phoneNumber: String = "",
    val message: String = "",
    val country: Country = Country()
) : LinkGeneratorEvent()

data class ButtonCopyClicked(
    val countryCode: String = "",
    val phoneNumber: String = "",
    val message: String = ""
) : LinkGeneratorEvent()

data class CountriesLoaded(val countries: List<Country>) : LinkGeneratorEvent()

data class CountriesException(val errorMessage: String) : LinkGeneratorEvent()

object FormInvalid : LinkGeneratorEvent()