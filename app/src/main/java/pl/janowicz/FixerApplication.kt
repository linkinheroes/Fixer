package pl.janowicz

import android.app.Application
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import pl.janowicz.fixer.api.DateAdapter
import pl.janowicz.fixer.api.FixerService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class FixerApplication : Application() {

    private val appModule = module {
        single {
            Moshi.Builder().add(DateAdapter()).add(KotlinJsonAdapterFactory()).build()
        }
        single {
            Retrofit.Builder()
                .client(get())
                .baseUrl(API_BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(get()))
                .build()
        }
        factory { get<Retrofit>().create(FixerService::class.java) }
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
    }
}