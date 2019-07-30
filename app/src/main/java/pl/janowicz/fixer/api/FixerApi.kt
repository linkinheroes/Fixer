package pl.janowicz.fixer.api

import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface FixerApi {

    @GET("{date}")
    fun getExchangeRates(
        @Path("date") date: String
    ): Single<Response<ExchangeRatesResponse>>
}