package pl.janowicz.fixer.ui.list

import androidx.lifecycle.LiveData
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

    private val _downloadedDays = mutableListOf<ExchangeRateDay>()
    val downloadedDays: List<ExchangeRateDay> get() = _downloadedDays

    private val _exchangeRatesDay = SingleLiveEvent<ExchangeRateDay>()
    val exchangeRatesDay: LiveData<ExchangeRateDay> get() = _exchangeRatesDay

    private val _initialExchangeRatesDay = SingleLiveEvent<ExchangeRateDay>()
    val initialExchangeRatesDay: LiveData<ExchangeRateDay> get() = _initialExchangeRatesDay

    private val _initialLoading = MutableLiveData<Boolean>()
    val initialLoading: LiveData<Boolean> get() = _initialLoading

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> get() = _loading

    private val _errorMessage = SingleLiveEvent<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    init {
        getTodayRates()
    }

    fun getTodayRates() = viewModelScope.launch(Dispatchers.IO) {
        lastDownloadedDay = Calendar.getInstance()
        _initialLoading.postValue(true)
        downloadExchangeRates(lastDownloadedDay.time)?.let { exchangeRateDay ->
            _downloadedDays.apply {
                clear()
                add(exchangeRateDay)
            }
            _initialExchangeRatesDay.postValue(exchangeRateDay)
        }
        _initialLoading.postValue(false)
    }

    fun getPreviousDayRates() = viewModelScope.launch(Dispatchers.IO) {
        lastDownloadedDay.add(Calendar.DATE, -1)
        _loading.postValue(true)
        downloadExchangeRates(lastDownloadedDay.time)?.let { exchangeRateDay ->
            _downloadedDays.add(exchangeRateDay)
            _exchangeRatesDay.postValue(exchangeRateDay)
        }
        _loading.postValue(false)
    }

    private suspend fun downloadExchangeRates(date: Date): ExchangeRateDay? {
        return when (val result = exchangeRatesRepository.getExchangeRates(date)) {
            is Result.Success -> {
                result.value.convertToExchangeRatesDay()
            }
            is Result.Error -> {
                lastDownloadedDay.add(Calendar.DATE, 1)
                _errorMessage.postValue(result.message)
                null
            }
        }
    }

    private fun ExchangeRatesResponse.convertToExchangeRatesDay(): ExchangeRateDay {
        return ExchangeRateDay(dayHeaderDateFormat.format(date), rates.map {
            ExchangeRateRow(it.key, it.value)
        })
    }

    companion object {
        const val LEFT_ITEMS_TO_LOAD_NEXT = 20
    }

}