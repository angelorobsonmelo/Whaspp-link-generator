package br.com.angelorobson.whatsapplinkgenerator.model.entities

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "created_at")
    val createdAt: String,
    @Embedded val countryEntity: CountryEntity
)