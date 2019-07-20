package pl.janowicz.fixer.ui.list.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.exchange_rate_row.view.*
import pl.janowicz.fixer.ui.list.ExchangeRateRow

class CurrencyRateRowViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(exchangeRateRow: ExchangeRateRow) = with(itemView) {
        exchange_rate_currency_text_view.text = exchangeRateRow.currency
        exchange_rate_text_view.text = exchangeRateRow.rate
    }
}