package br.com.angelorobson.whatsapplinkgenerator.model.domains

data class History(
    val id: Int? = null,
    val createdAt: String,
    val message: String,
    val phoneNumber: String,
    val country: Country
)