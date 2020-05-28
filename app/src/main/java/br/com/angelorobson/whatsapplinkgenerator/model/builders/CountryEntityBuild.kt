package br.com.angelorobson.whatsapplinkgenerator.model.builders

import br.com.angelorobson.whatsapplinkgenerator.model.entities.CountryEntity

class CountryEntityBuild {

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

        fun oneCountryEntity() = apply {
            countryFullName = "Brazil"
            countryShortName = "BR"
            areaCode = "+55"
            flag = "https://restcountries.eu/data/bra.svg"
        }

        fun build() = CountryEntity(
            countryFullName = countryFullName,
            countryShortName = countryShortName,
            areaCode = areaCode,
            flag = flag
        )
    }
}