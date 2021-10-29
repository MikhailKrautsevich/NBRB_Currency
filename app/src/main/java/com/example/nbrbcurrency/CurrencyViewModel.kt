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
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.observers.DisposableSingleObserver
import retrofit2.Retrofit
import java.util.*
import kotlin.collections.ArrayList

class CurrencyViewModel(app: Application) : AndroidViewModel(app) {

    companion object{
        const val LOG = "NBRBCOMMONLOG"
    }

    private val settingsDB: SettingsDataBase
    private val dao: SettingsDao
    private val currencies: MutableLiveData<Array<List<CurrencyData>>> = MutableLiveData()
    private val currenciesSettings: MutableLiveData<List<CurrencySettingContainer>> =
        MutableLiveData()
    private val settingsAvailable : MutableLiveData<Boolean> = MutableLiveData()

    private var disposable: Disposable? = null

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

        disposable = coursesData?.subscribeWith(object :
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
    }

    private fun getSettings() {

    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    fun getCurrenciesData() = (currencies as LiveData<Array<List<CurrencyData>>>)

    fun getSettingsAvailable() = (settingsAvailable as LiveData<Boolean>)

    fun getCurrencyByCharCode(charCode: String): CurrencyData {
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

    private fun postValueToSettingsAvailable(bool : Boolean) {
        settingsAvailable.postValue(bool)
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