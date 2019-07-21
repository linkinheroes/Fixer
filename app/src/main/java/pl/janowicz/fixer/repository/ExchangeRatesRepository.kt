package pl.janowicz.fixer.repository

import android.content.Context
import pl.janowicz.fixer.R
import pl.janowicz.fixer.api.ExchangeRatesResponse
import pl.janowicz.fixer.api.FixerApi
import java.text.SimpleDateFormat
import java.util.*

class ExchangeRatesRepository(
    private val context: Context,
    private val fixerApi: FixerApi
) {

    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun getExchangeRates(date: Date): Result<ExchangeRatesResponse> {
        return try {
            val response = fixerApi.getExchangeRates(apiDateFormat.format(date))
            if (response.isSuccessful) {
                val exchangeRatesResponse = response.body()
                if (exchangeRatesResponse != null) {
                    Result.Success(exchangeRatesResponse)
                } else {
                    Result.Error(context.getString(R.string.api_error_wrong_response))
                }
            } else {
                Result.Error(context.getString(R.string.api_error_server, response.code()))
            }
        } catch (e: Exception) {
            Result.Error(context.getString(R.string.api_error_connection))
        }
    }
}