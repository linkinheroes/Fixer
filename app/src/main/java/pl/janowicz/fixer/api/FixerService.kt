package pl.janowicz.fixer.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FixerService {

    @GET("{date}")
    suspend fun getExchangeRates(
        @Path("date") date: String
    ): Response<ExchangeRatesResponse>
}