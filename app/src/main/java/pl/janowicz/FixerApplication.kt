package pl.janowicz

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class FixerApplication : Application() {

    private val appModule = module {
        single {
            Retrofit.Builder()
                .client(get())
                .baseUrl("")
                .addConverterFactory(MoshiConverterFactory.create(get()))
                .build()
        }
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            //androidLogger(level = Level.DEBUG)
            androidContext(this@FixerApplication)
            modules(appModule)
        }
    }
}