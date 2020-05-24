package br.com.angelorobson.whatsapplinkgenerator.model.entities

import androidx.room.ColumnInfo

data class CountryEntity(
    @ColumnInfo(name = "country_full_name")
    val countryFullName: String,
    val flag: String,
    @ColumnInfo(name = "area_code")
    val areaCode: String,
    @ColumnInfo(name = "country_short_name")
    val countryShortName: String
)