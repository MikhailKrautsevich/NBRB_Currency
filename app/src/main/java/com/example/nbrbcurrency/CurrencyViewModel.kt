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
                    return@BiFunction t2
                } else return@BiFunction t1
            }).subscribeOn(Schedulers.io())

        val disposable : Disposable? = resultSettings
            .subscribeWith(
            object : DisposableSingleObserver<List<CurrencySettingContainer>>() {
                override fun onSuccess(t: List<CurrencySettingContainer>?) {
                    currenciesSettings.postValue(t)
                    Log.d(LOG, "suc")
                }

                override fun onError(e: Throwable?) {
                    currenciesSettings.postValue(ArrayList<CurrencySettingContainer>())
                    Log.d(LOG, "fail")
                    Log.d(LOG, e.toString())
                }
            }
        )
        compositeDisposable?.add(disposable)
    }

    fun getCurrenciesData() = (currencies as LiveData<Array<List<CurrencyData>>>)

    fun getSettingsAvailable() = (settingsAvailable as LiveData<Boolean>)

    fun getSettings() = (currenciesSettings as LiveData<List<CurrencySettingContainer>>)

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
        list.add(CurrencySettingContainer(RUB_CHARCODE, "100", true, 1))

//        val rub = getCurrencyByCharCode(RUB_CHARCODE)
//        val eur = getCurrencyByCharCode(EUR_CHARCODE)
//        val usd = getCurrencyByCharCode(USD_CHARCODE)
//
//        rub.let { list.add(CurrencySettingContainer(rub.charCode, rub.scale, true, 1)) }
//        eur.let { list.add(CurrencySettingContainer(eur.charCode, eur.scale, true, 2)) }
//        usd.let { list.add(CurrencySettingContainer(usd.charCode, usd.scale, true, 3)) }

//        list.let {
//            var i = 3
//            for (currency in list) {
//                if (currency.charCode != RUB_CHARCODE
//                    || currency.charCode != EUR_CHARCODE
//                    || currency.charCode != USD_CHARCODE
//                ) {
//                    i++
//                    list.add(
//                        CurrencySettingContainer(
//                            currency.charCode,
//                            currency.scale,
//                            false,
//                            i
//                        )
//                    )
//                }
//            }
//        }
        Log.d(LOG, list.size.toString())
        return list
    }


//    fun getAUD() = currencies.value?.get(0)?.get(0)
//
//    fun getAMD() = currencies.value?.get(0)?.get(1)
//
//    fun getBGN() = currencies.value?.get(0)?.get(2)
//
//    fun getUAH() = currencies.value?.get(0)?.get(3)
//
//    fun getDKK() = currencies.value?.get(0)?.get(4)
//
//    fun getUSD() = currencies.value?.get(0)?.get(5)
//
//    fun getEUR() = currencies.value?.get(0)?.get(6)
//
//    fun getPLN() = currencies.value?.get(0)?.get(7)
//
//    fun getJPY() = currencies.value?.get(0)?.get(8)
//
//    fun getIRR() = currencies.value?.get(0)?.get(9)
//
//    fun getISK() = currencies.value?.get(0)?.get(10)
//
//    fun getCAD() = currencies.value?.get(0)?.get(11)
//
//    fun getCNY() = currencies.value?.get(0)?.get(12)
//
//    fun getKWD() = currencies.value?.get(0)?.get(13)
//
//    fun getMDL() = currencies.value?.get(0)?.get(14)
//
//    fun getNZD() = currencies.value?.get(0)?.get(15)
//
//    fun getNOK() = currencies.value?.get(0)?.get(16)
//
//    fun getRUB() = currencies.value?.get(0)?.get(17)
//
//    fun getXDR() = currencies.value?.get(0)?.get(18)
//
//    fun getSGD() = currencies.value?.get(0)?.get(19)
//
//    fun getKGS() = currencies.value?.get(0)?.get(20)
//
//    fun getKZT() = currencies.value?.get(0)?.get(21)
//
//    fun getTRY() = currencies.value?.get(0)?.get(22)
//
//    fun getGBP() = currencies.value?.get(0)?.get(23)
//
//    fun getCZK() = currencies.value?.get(0)?.get(24)
//
//    fun getSEK() = currencies.value?.get(0)?.get(25)
//
//    fun getCHF() = currencies.value?.get(0)?.get(26)
}