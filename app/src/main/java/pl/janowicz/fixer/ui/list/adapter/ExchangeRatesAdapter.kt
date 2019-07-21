package pl.janowicz.fixer.ui.list.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.janowicz.fixer.R
import pl.janowicz.fixer.ui.list.ExchangeRateDay
import pl.janowicz.fixer.util.inflate
import kotlin.properties.Delegates

class ExchangeRatesAdapter(private val onExchangeRateClick: (date: String, currencyName: String, rate: String) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val exchangeRatesDayList = mutableListOf<ExchangeRateDay>()

    private val headersPositions = mutableListOf<Int>()

    private var itemCount = 0

    var showLoading: Boolean by Delegates.observable(false) { _, old, new ->
        if (new != old) {
            if (new) {
                itemCount += 1
                notifyItemInserted(itemCount - 1)
            } else {
                itemCount -= 1
                notifyItemRemoved(itemCount)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = when (viewType) {
        HEADER -> DayHeaderViewHolder(
            parent.inflate(R.layout.exchange_rate_day_header)
        )
        LOADING -> ExchangeRateLoadingViewHolder(parent.inflate(R.layout.layout_exchange_rate_list_loading))
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
                        val exchangeRateRow = day.rates[rateIndex]
                        holder.bind(exchangeRateRow) {
                            onExchangeRateClick(day.dateHeader, exchangeRateRow.currency, exchangeRateRow.rate)
                        }
                        break@loop
                    } else {
                        counter = lastRateInDayPosition
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position in headersPositions -> HEADER
            position == itemCount - 1 && showLoading -> LOADING
            else -> ROW
        }
    }

    fun add(exchangeRateDay: ExchangeRateDay) {
        headersPositions.add(itemCount)
        exchangeRatesDayList.add(exchangeRateDay)
        val currentItemCount = itemCount
        val elementsToAddCount = exchangeRateDay.rates.size + 1
        itemCount += elementsToAddCount
        notifyItemRangeInserted(currentItemCount, elementsToAddCount)
    }

    fun replace(exchangeRateDay: ExchangeRateDay) {
        exchangeRatesDayList.apply {
            clear()
            add(exchangeRateDay)
        }
        itemCount = exchangeRateDay.rates.size + 1
        headersPositions.apply {
            clear()
            add(0)
        }
        notifyDataSetChanged()
    }


    companion object {
        private const val HEADER = 0
        private const val ROW = 1
        private const val LOADING = 2
    }
}