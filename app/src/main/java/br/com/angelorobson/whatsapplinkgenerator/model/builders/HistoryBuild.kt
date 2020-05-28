package br.com.angelorobson.whatsapplinkgenerator.model.builders

import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import br.com.angelorobson.whatsapplinkgenerator.model.domains.History

class HistoryBuild {

    data class Builder(
        private var id: Int? = null,
        private var createdAt: String = "",
        private var message: String = "",
        private var phoneNumber: String = "",
        private var country: Country = Country()
    ) {

        fun id(id: Int?) = apply { this.id = id }
        fun createdAt(createdAt: String) = apply { this.createdAt = createdAt }
        fun message(message: String) = apply { this.message = message }
        fun phoneNumber(phoneNumber: String) = apply { this.phoneNumber = phoneNumber }
        fun country(country: Country) = apply { this.country = country }

        fun oneHistory() = apply {
            id = null
            createdAt = "12/02/2020 17:20"
            message = "Message sent"
            phoneNumber = "82994441587"
            country = CountryBuild.Builder()
                .oneCountry().build()
        }

        fun build() = History(
            id = id,
            createdAt = createdAt,
            message = message,
            phoneNumber = phoneNumber,
            country = country
        )
    }
}