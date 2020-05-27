package br.com.angelorobson.whatsapplinkgenerator.di

import android.content.Context
import br.com.angelorobson.whatsapplinkgenerator.model.database.dao.HistoryDao
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.ActivityService
import br.com.angelorobson.whatsapplinkgenerator.ui.utils.IdlingResource
import br.com.angelorobson.whatsapplinkgenerator.utils.TestIdlingResource
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class, ApiModule::class, TestModule::class])
interface TestComponent : ApplicationComponent {

    fun idlingResource(): IdlingResource
    override fun activityService(): ActivityService


    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        @BindsInstance
        fun historyDao(historyDao: HistoryDao): Builder

        fun build(): TestComponent
    }
}

@Module
object TestModule {

    @Provides
    @Singleton
    @JvmStatic
    fun idlingResource(): IdlingResource = TestIdlingResource()
}