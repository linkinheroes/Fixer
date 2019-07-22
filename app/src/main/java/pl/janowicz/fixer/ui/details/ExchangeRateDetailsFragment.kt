package pl.janowicz.fixer.ui.details

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.android.synthetic.main.fragment_exchange_rate_details.*
import pl.janowicz.fixer.R

class ExchangeRateDetailsFragment : Fragment(R.layout.fragment_exchange_rate_details) {

    private val exchangeRateDetailsFragmentArgs: ExchangeRateDetailsFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exchangeRateDetailsFragmentArgs.apply {
            exchange_rate_details_date_text_view.text = date
            exchange_rate_details_currency_name_text_view.text = currencyName
            exchange_rate_details_currency_rate_text_view.text = currencyRate
        }
        exchange_rate_details_back_image_view.setOnClickListener {
            findNavController().popBackStack()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            exchange_rate_details_constraint_layout.setOnApplyWindowInsetsListener { v, insets ->
                v.updatePadding(top = insets.systemWindowInsetTop)
                insets
            }
        }
    }
}