package br.com.angelorobson.whatsapplinkgenerator.model.repositories

import br.com.angelorobson.whatsapplinkgenerator.model.database.dao.HistoryDao
import br.com.angelorobson.whatsapplinkgenerator.model.domains.Country
import br.com.angelorobson.whatsapplinkgenerator.model.domains.History
import br.com.angelorobson.whatsapplinkgenerator.model.entities.CountryEntity
import br.com.angelorobson.whatsapplinkgenerator.model.entities.HistoryEntity
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {

    fun saveHistory(history: History): Completable {
        return Single.fromCallable { mapToHistoryEntity(history) }
            .flatMapCompletable { historyEntity ->
                historyDao.insert(historyEntity)
            }
    }

    fun getAll(): Observable<List<History>> {
        return historyDao.getAll()
            .map { historyEntities ->
                historyEntities.map { historyEntity ->
                    mapToHistory(historyEntity)
                }
            }
    }

}


private fun mapToHistoryEntity(history: History): HistoryEntity {
    val country = history.country
    return HistoryEntity(
        createdAt = history.createdAt,
        message = history.message,
        countryEntity = CountryEntity(
            countryFullName = country.countryFullName,
            countryShortName = country.countryShortName,
            areaCode = country.areaCode,
            flag = country.flag
        )
    )
}

private fun mapToHistory(entity: HistoryEntity): History {
    val countryEntity = entity.countryEntity
    return History(
        createdAt = entity.createdAt,
        message = entity.message,
        country = Country(
            countryFullName = countryEntity.countryFullName,
            countryShortName = countryEntity.countryShortName,
            areaCode = countryEntity.areaCode,
            flag = countryEntity.flag
        )
    )

}