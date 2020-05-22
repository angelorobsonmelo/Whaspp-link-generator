package br.com.angelorobson.whatsapplinkgenerator.linkgenerator

import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country

sealed class LinkGeneratorEvent

object Initial : LinkGeneratorEvent()

data class CountriesLoaded(val countries: List<Country>) : LinkGeneratorEvent()

data class CountriesApiException(val errorMessage: String) : LinkGeneratorEvent()

object ButtonSendClicked : LinkGeneratorEvent()

object FormInvalid : LinkGeneratorEvent()

object ButtonShareLinkClicked : LinkGeneratorEvent()