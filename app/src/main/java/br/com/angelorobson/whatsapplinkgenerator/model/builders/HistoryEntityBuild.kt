package br.com.angelorobson.whatsapplinkgenerator.model.builders

import br.com.angelorobson.whatsapplinkgenerator.model.entities.CountryEntity
import br.com.angelorobson.whatsapplinkgenerator.model.entities.HistoryEntity

class HistoryEntityBuild {

    data class Builder(
        private var id: Int = 0,
        private var createdAt: String = "",
        private var message: String = "",
        private var phoneNumber: String = "",
        private var countryEntity: CountryEntity? = null
    ) {

        fun id(id: Int) = apply { this.id = id }
        fun createdAt(createdAt: String) = apply { this.createdAt = createdAt }
        fun message(message: String) = apply { this.message = message }
        fun phoneNumber(phoneNumber: String) = apply { this.phoneNumber = phoneNumber }
        fun countryEntity(countryEntity: CountryEntity) =
            apply { this.countryEntity = countryEntity }

        fun oneHistory() = apply {
            id = 1
            createdAt = "12/02/2020"
            message = "Message sent"
            phoneNumber = "82994441587"
            countryEntity = CountryEntity(
                countryFullName = "Brazil",
                countryShortName = "BR",
                areaCode = "+55",
                flag = "https://restcountries.eu/data/bra.svg"
            )
        }

        fun build() = HistoryEntity(
            id = id,
            createdAt = createdAt,
            message = message,
            phoneNumber = phoneNumber,
            countryEntity = countryEntity!!
        )
    }
}