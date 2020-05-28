package br.com.angelorobson.whatsapplinkgenerator

import br.com.angelorobson.whatsapplinkgenerator.di.DaggerTestComponent
import br.com.angelorobson.whatsapplinkgenerator.model.database.dao.HistoryDao
import br.com.angelorobson.whatsapplinkgenerator.model.entities.HistoryEntity
import br.com.angelorobson.whatsapplinkgenerator.ui.App
import br.com.angelorobson.whatsapplinkgenerator.ui.component
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

import io.reactivex.subjects.PublishSubject

class AndroidTestApplication : App() {

    val historyEntitySubject = PublishSubject.create<List<HistoryEntity>>()

    private val historyDao: HistoryDao = object : HistoryDao() {
        override fun getAll(): Observable<List<HistoryEntity>> {
            return historyEntitySubject
        }

        override fun insert(historyEntity: HistoryEntity): Completable {
            return Completable.complete()
        }

    }

    override val component by lazy {
        DaggerTestComponent.builder()
            .context(this)
            .historyDao(historyDao)
            .build()
    }
}