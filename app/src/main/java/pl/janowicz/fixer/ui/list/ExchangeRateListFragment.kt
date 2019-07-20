package pl.janowicz.fixer.ui.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_exchange_rate_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.janowicz.fixer.R
import pl.janowicz.fixer.ui.list.adapter.ExchangeRatesAdapter
import pl.janowicz.fixer.util.SpaceItemDecoration

class ExchangeRateListFragment : Fragment(R.layout.fragment_exchange_rate_list) {

    private val exchangeRateListViewModel: ExchangeRateListViewModel by viewModel()

    private val exchangeRatesAdapter = ExchangeRatesAdapter { date, currencyName, rate ->
        val directions = ExchangeRateListFragmentDirections.actionExchangeRateListFragmentToExchangeRateDetailsFragment(
            date,
            currencyName,
            rate
        )
        findNavController().navigate(directions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exchangeRateListViewModel.loading.observe(viewLifecycleOwner, Observer {
            exchange_rate_list_swipe_refresh_layout?.isRefreshing = it
        })
        exchangeRateListViewModel.exchangeRatesDay.observe(viewLifecycleOwner, Observer {
            exchangeRatesAdapter.addExchangeRatesDay(it)
        })
        exchangeRateListViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            Snackbar.make(exchange_rate_list_coordinator_layout, it, Snackbar.LENGTH_LONG).show()
        })
        exchangeRateListViewModel.downloadedDays.forEach {
            exchangeRatesAdapter.addExchangeRatesDay(it)
        }
        exchange_rate_list_recycler_view.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = exchangeRatesAdapter
            val itemSpaceVertical = resources.getDimension(R.dimen.exchange_rate_list_items_space_vertical).toInt()
            val itemSpaceHorizontal = resources.getDimension(R.dimen.exchange_rate_list_items_space_horizontal).toInt()
            addItemDecoration(SpaceItemDecoration(itemSpaceVertical, itemSpaceHorizontal))
        }
    }
}