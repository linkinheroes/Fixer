package pl.janowicz.fixer.ui.list

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_exchange_rate_list.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import pl.janowicz.fixer.R
import pl.janowicz.fixer.ui.list.adapter.ExchangeRatesAdapter
import pl.janowicz.fixer.util.EndlessScrollRecyclerListener
import pl.janowicz.fixer.util.SpaceItemDecoration

class ExchangeRateListFragment : Fragment(R.layout.fragment_exchange_rate_list) {

    private val exchangeRateListViewModel: ExchangeRateListViewModel by viewModel()

    private val exchangeRatesAdapter = ExchangeRatesAdapter { date, currencyName, rate ->
        ExchangeRateListFragmentDirections.actionExchangeRateListFragmentToExchangeRateDetailsFragment(
            date,
            currencyName,
            rate
        ).let {
            findNavController().navigate(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exchangeRateListViewModel.initialLoading.observe(viewLifecycleOwner, Observer {
            exchange_rate_list_swipe_refresh_layout.isRefreshing = it
        })
        exchangeRateListViewModel.loading.observe(viewLifecycleOwner, Observer {
            exchangeRatesAdapter.showLoading = it
        })
        exchangeRateListViewModel.initialExchangeRatesDay.observe(viewLifecycleOwner, Observer {
            exchangeRatesAdapter.replace(it)
            exchange_rate_list_recycler_view.scheduleLayoutAnimation()
        })
        exchangeRateListViewModel.exchangeRatesDay.observe(viewLifecycleOwner, Observer {
            exchangeRatesAdapter.add(it)
        })
        exchangeRateListViewModel.errorMessage.observe(viewLifecycleOwner, Observer {
            Snackbar.make(exchange_rate_list_coordinator_layout, it, Snackbar.LENGTH_INDEFINITE).show()
        })
        exchangeRateListViewModel.downloadedDays.forEach {
            exchangeRatesAdapter.add(it)
        }
        exchange_rate_list_recycler_view.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = exchangeRatesAdapter
            val itemSpaceVertical = resources.getDimension(R.dimen.exchange_rate_list_items_space_vertical).toInt()
            val itemSpaceHorizontal = resources.getDimension(R.dimen.exchange_rate_list_items_space_horizontal).toInt()
            addItemDecoration(SpaceItemDecoration(itemSpaceVertical, itemSpaceHorizontal))
            addOnScrollListener(EndlessScrollRecyclerListener(ExchangeRateListViewModel.LEFT_ITEMS_TO_LOAD_NEXT) {
                if (!exchangeRateListViewModel.loading.value!!) {
                    exchangeRateListViewModel.getPreviousDayRates()
                }
            })
        }
        exchange_rate_list_swipe_refresh_layout.setOnRefreshListener {
            exchangeRateListViewModel.getTodayRates()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            exchange_rate_list_coordinator_layout.setOnApplyWindowInsetsListener { v, insets ->
                v.updatePadding(top = insets.systemWindowInsetTop)
                insets
            }
        }
    }
}