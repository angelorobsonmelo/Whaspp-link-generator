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

        fun oneHistoryEntity() = apply {
            id = 1
            createdAt = "2020-05-27T11:24:43.644"
            message = "Message sent"
            phoneNumber = "82994441587"
            countryEntity = CountryEntityBuild.Builder().oneCountryEntity().build()
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