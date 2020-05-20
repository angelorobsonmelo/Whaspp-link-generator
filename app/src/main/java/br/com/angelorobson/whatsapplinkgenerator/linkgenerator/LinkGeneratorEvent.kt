package br.com.angelorobson.whatsapplinkgenerator.linkgenerator

import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country

sealed class LinkGeneratorEvent

object Initial : LinkGeneratorEvent()

data class CountriesLoaded(val countries: List<Country>) : LinkGeneratorEvent()

object ButtonSendClicked : LinkGeneratorEvent()