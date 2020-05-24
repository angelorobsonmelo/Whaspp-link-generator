package br.com.angelorobson.whatsapplinkgenerator.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.angelorobson.whatsapplinkgenerator.model.database.dao.HistoryDao
import br.com.angelorobson.whatsapplinkgenerator.model.entities.HistoryEntity

@Database(
    entities = [HistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ApplicationDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao


}