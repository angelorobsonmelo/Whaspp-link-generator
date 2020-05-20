package br.com.angelorobson.whatsapplinkgenerator.linkgenerator

import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country

data class LinkGeneratorModel(
    val countries: List<Country> = listOf(),
    val error: Boolean = false,
    val isLoading: Boolean = false
)