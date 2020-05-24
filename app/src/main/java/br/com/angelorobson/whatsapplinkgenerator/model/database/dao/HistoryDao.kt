package br.com.angelorobson.whatsapplinkgenerator.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import br.com.angelorobson.whatsapplinkgenerator.model.entities.HistoryEntity
import io.reactivex.Completable
import io.reactivex.Observable

@Dao
abstract class HistoryDao {

    @Query("SELECT * FROM HistoryEntity")
    abstract fun get(): Observable<List<HistoryEntity>>

    @Insert
    abstract fun insert(historyEntity: HistoryEntity): Completable

}