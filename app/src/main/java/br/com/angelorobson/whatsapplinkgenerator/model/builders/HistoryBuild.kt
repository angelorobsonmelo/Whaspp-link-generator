package br.com.angelorobson.whatsapplinkgenerator.model.builders

import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import br.com.angelorobson.whatsapplinkgenerator.model.domains.History

class HistoryBuild {

    data class Builder(
        var id: Int? = null,
        var createdAt: String = "",
        var message: String = "",
        var phoneNumber: String = "",
        var country: Country = Country()
    ) {

        fun id(id: Int) = apply { this.id = id }
        fun createdAt(createdAt: String) = apply { this.createdAt = createdAt }
        fun message(message: String) = apply { this.message = message }
        fun phoneNumber(phoneNumber: String) = apply { this.phoneNumber = phoneNumber }
        fun country(country: Country) = apply { this.country = country }

        fun oneHistory() =
            History(
                id = 1,
                createdAt = "12/02/2020",
                message = "Message sent",
                phoneNumber = "82994441587",
                country = Country(
                    countryFullName = "Brazil",
                    countryShortName = "BR",
                    flag = "https://restcountries.eu/data/bra.svg"
                )
            )

        fun build() = History(
            id = id,
            createdAt = createdAt,
            message = message,
            phoneNumber = phoneNumber,
            country = country
        )
    }
}