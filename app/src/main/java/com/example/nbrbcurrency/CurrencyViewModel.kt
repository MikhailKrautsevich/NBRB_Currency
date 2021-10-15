package com.example.nbrbcurrency

import androidx.lifecycle.ViewModel
import com.example.nbrbcurrency.retrofit.CurrencyApi
import com.example.nbrbcurrency.retrofit.RetrofitHelper
import com.example.nbrbcurrency.retrofit.models.CurrencyDataList
import com.example.nbrbcurrency.utils.DateHelper
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import java.util.*

class CurrencyViewModel : ViewModel() {
    fun getCurrencyData() :Single<CurrencyDataList>?{
        val date = Date()
        val currentDate = DateHelper.getDateForRetrofit(date)
        val tomorrowDate = DateHelper.getTomorrowDateForRetrofit(date)

        val retrofit: Retrofit = RetrofitHelper.getRetrofit()
        val api: CurrencyApi? = RetrofitHelper.getApi(retrofit)

        val currentCourses:Single<CurrencyDataList>? = api?.getCurrencyListSingle(currentDate)
        val tomorrowCourses:Single<CurrencyDataList>? = api?.getCurrencyListSingle(tomorrowDate)

        return currentCourses?.subscribeOn(Schedulers.newThread())
    }
}