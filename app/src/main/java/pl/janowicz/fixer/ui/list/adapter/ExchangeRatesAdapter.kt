package pl.janowicz.fixer.ui.list.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.janowicz.fixer.R
import pl.janowicz.fixer.ui.list.ExchangeRatesDay
import pl.janowicz.fixer.util.inflate

class ExchangeRatesAdapter(onExchangeRateClick: (exchangeRateDay: ExchangeRatesDay) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val exchangeRatesDayList = mutableListOf<ExchangeRatesDay>()

    private val headersPositions = mutableListOf<Int>()

    private var itemCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        HEADER -> DayHeaderViewHolder(
            parent.inflate(R.layout.exchange_rate_day_header)
        )
        else -> CurrencyRateRowViewHolder(parent.inflate(R.layout.exchange_rate_row))
    }

    override fun getItemCount() = itemCount

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is DayHeaderViewHolder -> {
                val dayIndex = headersPositions.indexOf(position)
                holder.bind(exchangeRatesDayList[dayIndex].dateHeader)
            }
            is CurrencyRateRowViewHolder -> {
                var counter = 0
                loop@ for (day in exchangeRatesDayList) {
                    val lastRateInDayPosition = counter + day.rates.size + 1
                    if (position < lastRateInDayPosition) {
                        val rateIndex = position - counter - 1
                        holder.bind(day.rates[rateIndex])
                        break@loop
                    } else {
                        counter = lastRateInDayPosition
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position in headersPositions) {
            HEADER
        } else {
            ROW
        }
    }

    fun addExchangeRatesDay(exchangeRatesDay: ExchangeRatesDay) {
        headersPositions.add(itemCount)
        exchangeRatesDayList.add(exchangeRatesDay)
        val currentItemCount = itemCount
        val elementsToAddCount = exchangeRatesDay.rates.size + 1
        itemCount += elementsToAddCount
        notifyItemRangeInserted(currentItemCount, elementsToAddCount)
    }

    companion object {
        private const val HEADER = 0
        private const val ROW = 1
    }
}