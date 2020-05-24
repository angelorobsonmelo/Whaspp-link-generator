package br.com.angelorobson.whatsapplinkgenerator.model.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @ColumnInfo(name = "phone_number")
    val phoneNumber: String,
    val message: String,
    @Embedded val countryEntity: CountryEntity
)