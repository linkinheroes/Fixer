package pl.janowicz.fixer.ui.list

data class ExchangeRateDay(
    val dateHeader: String,
    val rates: List<ExchangeRateRow>
)