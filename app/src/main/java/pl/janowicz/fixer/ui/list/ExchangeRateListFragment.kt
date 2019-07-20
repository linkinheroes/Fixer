package pl.janowicz.fixer.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.janowicz.fixer.R

class ExchangeRateListFragment : Fragment(R.layout.fragment_exchange_rate_list) {

    private val exchangeRateListViewModel: ExchangeRateListViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exchangeRateListViewModel.getTodayRates()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}