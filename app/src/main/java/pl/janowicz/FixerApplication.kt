package pl.janowicz

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pl.janowicz.fixer.BuildConfig
import pl.janowicz.fixer.api.DateAdapter
import pl.janowicz.fixer.api.FixerApi
import pl.janowicz.fixer.repository.ExchangeRatesRepository
import pl.janowicz.fixer.ui.list.ExchangeRateListViewModel
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class FixerApplication : Application() {

    private val appModule = module {
        single {
            Moshi.Builder().add(DateAdapter()).add(KotlinJsonAdapterFactory()).build()
        }
        single {
            OkHttpClient.Builder().apply {
                addInterceptor {
                    val original = it.request()
                    val url = original.url.newBuilder()
                        .addQueryParameter(API_ACCESS_KEY_PARAM, BuildConfig.FixerAccessKey)
                        .build()
                    val requestBuilder = original.newBuilder().url(url)
                    it.proceed(requestBuilder.build())
                }
            }.build()
        }
        single {
            Retrofit.Builder()
                .client(get())
                .baseUrl(API_BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(get()))
                .build()
        }
        factory { get<Retrofit>().create(FixerApi::class.java) }
        single { ExchangeRatesRepository(get(), get()) }
        viewModel { ExchangeRateListViewModel(get()) }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            //androidLogger(level = Level.DEBUG)
            androidContext(this@FixerApplication)
            modules(appModule)
        }
    }

    companion object {
        private const val API_BASE_URL = "http://data.fixer.io/api/"
        private const val API_ACCESS_KEY_PARAM = "access_key"
    }
}