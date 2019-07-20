package pl.janowicz.fixer.ui.list.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.exchange_rate_day_header.view.*

class DayHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(date: String) = with(itemView) {
        exchange_rate_day_header_text_view.text = date
    }

}