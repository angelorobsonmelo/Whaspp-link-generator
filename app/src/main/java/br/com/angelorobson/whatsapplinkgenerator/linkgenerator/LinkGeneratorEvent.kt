package br.com.angelorobson.whatsapplinkgenerator.linkgenerator

import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country

sealed class LinkGeneratorEvent

object Initial : LinkGeneratorEvent()

data class CountriesLoaded(val countries: List<Country>) : LinkGeneratorEvent()

data class CountriesApiException(val errorMessage: String) : LinkGeneratorEvent()

data class ButtonSendClicked(
    val countryCode: String = "",
    val phoneNumber: String = "",
    val message: String = ""
) : LinkGeneratorEvent()

object FormInvalid : LinkGeneratorEvent()

object ButtonShareLinkClicked : LinkGeneratorEvent()