package br.com.angelorobson.whatsapplinkgenerator.ui.linkgenerator

import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country

data class LinkGeneratorModel(
    val linkGeneratorResult: LinkGeneratorResult = LinkGeneratorResult.Loading()
)

sealed class LinkGeneratorResult {
    data class Error(
        val errorMessage: String,
        val isLoading: Boolean = false
    ) : LinkGeneratorResult()

    data class Loading(val isLoading: Boolean = true) :
        LinkGeneratorResult()

    data class CountriesLoaded(
        val countries: List<Country> = listOf(),
        val isLoading: Boolean = false
    ) : LinkGeneratorResult()

    data class ContactInformationToSend(
        val countryCode: String = "",
        val phoneNumber: String = "",
        val message: String = ""
    ) : LinkGeneratorResult()

    data class ContactInformationToCopy(
        val countryCode: String = "",
        val phoneNumber: String = "",
        val message: String = ""
    ) : LinkGeneratorResult()

}