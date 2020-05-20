package br.com.angelorobson.whatsapplinkgenerator.model.dtos

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CountryDto(
    val name: String,
    val flag: String,
    val callingCodes: List<String>
)