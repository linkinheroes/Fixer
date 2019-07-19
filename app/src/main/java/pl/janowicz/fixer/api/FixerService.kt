package pl.janowicz.fixer.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

interface FixerService {

    @GET("{date}")
    suspend fun getExchangeRates(
        @Path("date") date: Date,
        @Query("access_key") accessKey: String
    ): Response<ExchangeRatesResponse>
}