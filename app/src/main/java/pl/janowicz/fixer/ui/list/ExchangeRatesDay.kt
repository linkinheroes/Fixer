package pl.janowicz.fixer.ui.list

data class ExchangeRatesDay(
    val dateHeader: String,
    val rates: List<ExchangeRateRow>
)