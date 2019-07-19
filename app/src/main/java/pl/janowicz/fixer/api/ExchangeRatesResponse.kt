package pl.janowicz.fixer.api

import java.util.*

data class ExchangeRatesResponse(
    val base: String,
    val date: Date,
    val historical: Boolean,
    val rates: Rates,
    val success: Boolean,
    val timestamp: Long
)