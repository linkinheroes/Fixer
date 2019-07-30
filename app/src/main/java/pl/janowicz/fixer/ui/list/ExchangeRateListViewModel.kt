package pl.janowicz.fixer.ui.list

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import pl.janowicz.fixer.R
import pl.janowicz.fixer.api.ExchangeRatesResponse
import pl.janowicz.fixer.api.FixerApi
import pl.janowicz.fixer.util.SingleLiveEvent
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ExchangeRateListViewModel(
    private val context: Context,
    private val fixerApi: FixerApi
) : ViewModel() {

    private val dayHeaderDateFormat = SimpleDateFormat("dd LLLL yyyy, EEEE", Locale.getDefault())

    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

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

    var disposables = CompositeDisposable()

    init {
        getTodayRates()
    }

    override fun onCleared() {
        disposables.clear()
    }

    fun getTodayRates() {
        lastDownloadedDay = Calendar.getInstance()
        disposables.add(
            fixerApi.getExchangeRates(apiDateFormat.format(lastDownloadedDay.time))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _initialLoading.postValue(true) }
                .subscribeBy(
                    onError = {
                        if (it is IOException) {
                            _errorMessage.postValue(context.getString(R.string.api_error_connection))
                        }
                        _initialLoading.postValue(false)
                    },
                    onSuccess = {
                        if (it.isSuccessful) {
                            val exchangeRatesResponse = it.body()
                            if (exchangeRatesResponse != null) {
                                val exchangeRateDay = exchangeRatesResponse.convertToExchangeRatesDay()
                                _downloadedDays.apply {
                                    clear()
                                    add(exchangeRateDay)
                                }
                                _initialExchangeRatesDay.postValue(exchangeRateDay)
                            } else {
                                _errorMessage.postValue(context.getString(R.string.api_error_wrong_response))
                            }
                        } else {
                            _errorMessage.postValue(context.getString(R.string.api_error_server, it.code()))
                        }
                        _initialLoading.postValue(false)
                    }
                )
        )
    }

    fun getPreviousDayRates() {
        lastDownloadedDay.add(Calendar.DATE, -1)
        disposables.add(
            fixerApi.getExchangeRates(apiDateFormat.format(lastDownloadedDay.time))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { _loading.postValue(true) }
                .subscribeBy(
                    onError = {
                        _loading.postValue(false)
                        if (it is IOException) {
                            _errorMessage.postValue(context.getString(R.string.api_error_connection))
                        }
                    },
                    onSuccess = {
                        _loading.postValue(false)
                        if (it.isSuccessful) {
                            val exchangeRatesResponse = it.body()
                            if (exchangeRatesResponse != null) {
                                val exchangeRateDay = exchangeRatesResponse.convertToExchangeRatesDay()
                                _downloadedDays.add(exchangeRateDay)
                                _exchangeRatesDay.postValue(exchangeRateDay)
                            } else {
                                _errorMessage.postValue(context.getString(R.string.api_error_wrong_response))
                            }
                        } else {
                            _errorMessage.postValue(context.getString(R.string.api_error_server, it.code()))
                        }
                    }
                )
        )
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