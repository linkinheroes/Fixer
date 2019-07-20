package pl.janowicz.fixer.ui.list

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.janowicz.fixer.api.ExchangeRatesResponse
import pl.janowicz.fixer.repository.ExchangeRatesRepository
import pl.janowicz.fixer.repository.Result
import pl.janowicz.fixer.util.SingleLiveEvent
import java.text.SimpleDateFormat
import java.util.*

class ExchangeRateListViewModel(
    private val exchangeRatesRepository: ExchangeRatesRepository
) : ViewModel() {

    private val dayHeaderDateFormat = SimpleDateFormat("dd LLLL yyyy, EEEE", Locale.getDefault())

    private lateinit var lastDownloadedDay: Calendar

    val downloadedDays = mutableListOf<ExchangeRateDay>()

    val exchangeRatesDay = SingleLiveEvent<ExchangeRateDay>()

    val loading = MutableLiveData<Boolean>()

    val errorMessage = SingleLiveEvent<String>()

    init {
        getTodayRates()
    }

    private fun getTodayRates() = viewModelScope.launch(Dispatchers.IO) {
        lastDownloadedDay = Calendar.getInstance()
        downloadExchangeRates(lastDownloadedDay.time)
    }

    fun getPreviousRates() = viewModelScope.launch(Dispatchers.IO) {
        lastDownloadedDay.add(Calendar.DATE, -1)
        downloadExchangeRates(lastDownloadedDay.time)
    }

    private suspend fun downloadExchangeRates(date: Date) {
        loading.postValue(true)
        val result = exchangeRatesRepository.getExchangeRates(date)
        loading.postValue(false)
        when (result) {
            is Result.Success -> {
                val exchangeRateDay = result.value.convertToExchangeRatesDay()
                downloadedDays.add(exchangeRateDay)
                exchangeRatesDay.postValue(exchangeRateDay)
            }
            is Result.Error -> {
                lastDownloadedDay.add(Calendar.DATE, 1)
                errorMessage.postValue(result.message)
            }
        }
    }

    private fun ExchangeRatesResponse.convertToExchangeRatesDay(): ExchangeRateDay {
        return ExchangeRateDay(dayHeaderDateFormat.format(date), rates.map {
            ExchangeRateRow(it.key, it.value)
        })
    }

}