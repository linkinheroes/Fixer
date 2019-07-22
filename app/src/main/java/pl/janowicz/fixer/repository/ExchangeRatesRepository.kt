package pl.janowicz.fixer.repository

import android.content.Context
import pl.janowicz.fixer.R
import pl.janowicz.fixer.api.ExchangeRatesResponse
import pl.janowicz.fixer.api.FixerApi
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ExchangeRatesRepository(
    private val context: Context,
    private val fixerApi: FixerApi
) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    suspend fun getExchangeRates(date: Date): Result<ExchangeRatesResponse> {
        return try {
            val dateInApiFormat = dateFormat.format(date)
            val response = fixerApi.getExchangeRates(dateInApiFormat)
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
        } catch (e: IOException) {
            Result.Error(context.getString(R.string.api_error_connection))
        }
    }
}