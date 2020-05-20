package br.com.angelorobson.whatsapplinkgenerator.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import br.com.angelorobson.whatsapplinkgenerator.R
import br.com.angelorobson.whatsapplinkgenerator.linkgenerator.LinkGeneratorViewModel
import br.com.angelorobson.whatsapplinkgenerator.model.services.CountryService
import br.com.angelorobson.whatsapplinkgenerator.utils.ActivityService
import br.com.angelorobson.whatsapplinkgenerator.utils.Navigator
import dagger.*
import dagger.multibindings.IntoMap
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Provider
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
internal annotation class ViewModelKey(val value: KClass<out ViewModel>)

@MustBeDocumented
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
internal annotation class ApiBaseUrl

interface ApplicationComponent {

    fun viewModelFactory(): ViewModelProvider.Factory

    fun activityService(): ActivityService

}

@Singleton
@Component(modules = [ApplicationModule::class, ViewModelModule::class, ApiModule::class])
interface RealComponent : ApplicationComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun context(context: Context): Builder

        fun build(): RealComponent
    }
}

@Module
object ApplicationModule {

    @Provides
    @Singleton
    @JvmStatic
    fun viewModels(viewModels: MutableMap<Class<out ViewModel>, Provider<ViewModel>>):
            ViewModelProvider.Factory {
        return ViewModelFactory(viewModels)
    }

    @Provides
    @Singleton
    @JvmStatic
    fun activityService(): ActivityService = ActivityService()

    @Provides
    @Singleton
    @JvmStatic
    fun navigator(activityService: ActivityService): Navigator {
        return Navigator(R.id.navigationHostFragment, activityService)
    }

    @Provides
    @ApiBaseUrl
    @JvmStatic
    fun apiBaseUrl(context: Context): String = context.getString(R.string.api_base_url)
}

@Module
abstract class ViewModelModule {


    @Binds
    @IntoMap
    @ViewModelKey(LinkGeneratorViewModel::class)
    abstract fun linkGeneratorViewModel(viewModel: LinkGeneratorViewModel): ViewModel
}


@Module
object ApiModule {

    @Provides
    @Singleton
    @JvmStatic
    fun okHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()
    }

    @Provides
    @Singleton
    @JvmStatic
    fun retrofit(@ApiBaseUrl apiBaseUrl: String, okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(apiBaseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
    }


    @Provides
    @Singleton
    @JvmStatic
    fun countryService(retrofit: Retrofit): CountryService {
        return retrofit.create(CountryService::class.java)
    }

}
