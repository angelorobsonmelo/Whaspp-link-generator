package br.com.angelorobson.whatsapplinkgenerator.model.builders

import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country

class CountryBuild {

    data class Builder(
        private var countryFullName: String = "",
        private var flag: String = "",
        private var areaCode: String = "",
        private var countryShortName: String = ""
    ) {

        fun countryFullName(countryFullName: String) =
            apply { this.countryFullName = countryFullName }

        fun flag(flag: String) = apply { this.flag = flag }
        fun areaCode(areaCode: String) = apply { this.areaCode = areaCode }
        fun countryShortName(countryShortName: String) =
            apply { this.countryShortName = countryShortName }

        fun oneCountry() = apply {
            countryFullName = "Brazil"
            flag = "https://restcountries.eu/data/bra.svg"
            areaCode = "+55"
            countryShortName = "BR"
        }

        fun build() = Country(
            countryFullName = countryFullName,
            flag = flag,
            areaCode = areaCode,
            countryShortName = countryShortName
        )
    }
}