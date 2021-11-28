package com.example.nbrbcurrency

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.nbrbcurrency.db.SettingsDataBase
import com.example.nbrbcurrency.db.CurrencySettingContainer
import com.example.nbrbcurrency.db.SettingsDao
import com.example.nbrbcurrency.retrofit.CurrencyApi
import com.example.nbrbcurrency.retrofit.RetrofitHelper
import com.example.nbrbcurrency.retrofit.models.CurrencyData
import com.example.nbrbcurrency.retrofit.models.CurrencyDataList
import com.example.nbrbcurrency.utils.DateHelper
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.CompletableObserver
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.Single.*
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList

class CurrencyViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        const val LOG = "NBRBCOMMONLOG"

        const val RUB_CHARCODE = "RUB"
        const val EUR_CHARCODE = "EUR"
        const val USD_CHARCODE = "USD"
    }

    private val settingsDB: SettingsDataBase
    private val dao: SettingsDao
    private var isItFirstLaunch = false

    private val currencies: MutableLiveData<Array<List<CurrencyData>>> = MutableLiveData()
    private val currenciesSettings: MutableLiveData<List<CurrencySettingContainer>> =
        MutableLiveData()
    private val settingsAvailable: MutableLiveData<Boolean> = MutableLiveData()

    private var compositeDisposable: CompositeDisposable? = null

    init {
        Log.d(LOG, "CurrencyViewModel: init()")
        getCurrencyData()

        settingsDB = SettingsDataBase.getDatabase(app.applicationContext)
        dao = settingsDB.dao
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable?.dispose()
    }

    fun getSettingsList() {
        val savedSettings: Single<List<CurrencySettingContainer>> = dao.getAll()
        val defaultSettings: Single<List<CurrencySettingContainer>> = just(getDefaultList())

        val resultSettings: Single<List<CurrencySettingContainer>> = savedSettings
            .zipWith(defaultSettings, BiFunction { t1, t2 ->
                if (t1.isEmpty()) {
                    isItFirstLaunch = true
                    return@BiFunction t2
                } else return@BiFunction t1
            }).subscribeOn(Schedulers.io())

        val disposable: Disposable? = resultSettings
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(
                object : DisposableSingleObserver<List<CurrencySettingContainer>>() {
                    override fun onSuccess(t: List<CurrencySettingContainer>?) {
                        currenciesSettings.postValue(t)
                        Log.d(LOG, "CurrencyViewModel: getSettingsList(): onSuccess()")
                    }

                    override fun onError(e: Throwable?) {
                        currenciesSettings.postValue(ArrayList<CurrencySettingContainer>())
                        Log.d(
                            LOG,
                            "CurrencyViewModel: getSettingsList(): onError = ${e?.localizedMessage}"
                        )
                    }
                }
            )
        compositeDisposable?.add(disposable)
    }

    fun getCurrenciesData() = (currencies as LiveData<Array<List<CurrencyData>>>)

    fun getSettingsAvailable() = (settingsAvailable as LiveData<Boolean>)

    fun getSettings() = (currenciesSettings as LiveData<List<CurrencySettingContainer>>)

    fun saveSettings(settings: List<CurrencySettingContainer>) {
        if (currenciesSettings.value != null
            || currenciesSettings.value != settings) {
            for (setting in settings) {
                val completable: Completable? = if (isItFirstLaunch) {
                    Completable.fromRunnable { dao.add(setting) }
                } else {
                    Completable.fromRunnable {
                        setting.position = settings.indexOf(setting)                                    // апдейтим позицию setting
                        dao.update(setting)
                    }
                }
                completable
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(Schedulers.io())
                    ?.subscribeWith(object : CompletableObserver {
                        override fun onSubscribe(d: Disposable?) {
                            Log.d(
                                LOG,
                                "CurrencyViewModel: saveSettings(): I try to save ${setting.position}"
                            )
                        }

                        override fun onComplete() {
                            if (isItFirstLaunch) {
                                Log.d(
                                    LOG,
                                    "CurrencyViewModel: saveSettings(): I saved ${setting.position}"
                                )
                            } else {
                                Log.d(
                                    LOG,
                                    "CurrencyViewModel: saveSettings(): I updated ${setting.position}"
                                )
                            }
                        }

                        override fun onError(e: Throwable?) {
                            Log.d(
                                LOG,
                                "CurrencyViewModel: saveSettings(): I did not save ${setting.position} because ${e.toString()}"
                            )
                        }
                    })
            }
        }
    }

    private fun getCurrencyData() {
        val date = Date()
        val currentDate = DateHelper.getDateForRetrofit(date)
        val tomorrowDate = DateHelper.getTomorrowDateForRetrofit(date)

        val retrofit: Retrofit = RetrofitHelper.getRetrofit()
        val api: CurrencyApi? = RetrofitHelper.getApi(retrofit)

        val currentCourses: Single<CurrencyDataList>? = api?.getCurrencyList(currentDate)
        val tomorrowCourses: Single<CurrencyDataList>? = api?.getCurrencyList(tomorrowDate)

        val coursesData: Single<Array<List<CurrencyData>>>? = currentCourses
            ?.zipWith(tomorrowCourses, BiFunction { t1, t2 ->
                return@BiFunction arrayOf(t1.currencies, t2.currencies)
            })
            ?.subscribeOn(Schedulers.computation())
            ?.observeOn(AndroidSchedulers.mainThread())

        val disposable: Disposable? =
            coursesData?.subscribeWith(object :
                DisposableSingleObserver<Array<List<CurrencyData>>>() {
                override fun onSuccess(t: Array<List<CurrencyData>>?) {
                    postValueToCurrencies(t)
                    postValueToSettingsAvailable(true)
                }

                override fun onError(e: Throwable?) {
                    val list: List<CurrencyData> = ArrayList()
                    postValueToCurrencies(arrayOf(list))
                    postValueToSettingsAvailable(false)
                }
            })
        compositeDisposable?.add(disposable)
    }

    private fun getCurrencyByCharCode(charCode: String): CurrencyData {
        currencies.value?.get(0)?.let {
            val list = currencies.value!![0]
            for (currency in list) {
                if (currency.charCode == charCode) return currency
            }
        }
        return CurrencyData()
    }

    private fun postValueToCurrencies(list: Array<List<CurrencyData>>?) {
        currencies.postValue(list)
    }

    private fun postValueToSettingsAvailable(bool: Boolean) {
        settingsAvailable.postValue(bool)
    }

    private fun getDefaultList(): List<CurrencySettingContainer> {
        val list = ArrayList<CurrencySettingContainer>()

        val rub = getCurrencyByCharCode(RUB_CHARCODE)
        val eur = getCurrencyByCharCode(EUR_CHARCODE)
        val usd = getCurrencyByCharCode(USD_CHARCODE)

        list.add(CurrencySettingContainer(rub.charCode, getScaleName(rub), true, 0))
        list.add(CurrencySettingContainer(eur.charCode, getScaleName(eur), true, 1))
        list.add(CurrencySettingContainer(usd.charCode, getScaleName(usd), true, 2))

        val data = currencies.value?.get(0)
        var position = 3

        data?.let {
            for (currency in data) {
                if (currency.charCode != RUB_CHARCODE
                    && currency.charCode != EUR_CHARCODE
                    && currency.charCode != USD_CHARCODE
                ) {
                    list.add(
                        CurrencySettingContainer(
                            currency.charCode,
                            getScaleName(currency),
                            false,
                            position
                        )
                    )
                    position++
                }
            }
        }

        Log.d(
            LOG,
            "CurrencyViewModel: getDefaultList(): Default List size = ${list.size.toString()}"
        )
        return list
    }

    private fun getScaleName(curr: CurrencyData): String {
        return String.format("%s %s", curr.scale, curr.name)
    }
}