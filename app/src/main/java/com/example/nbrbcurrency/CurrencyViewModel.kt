package com.example.nbrbcurrency

import androidx.lifecycle.ViewModel
import com.example.nbrbcurrency.retrofit.CurrencyApi
import com.example.nbrbcurrency.retrofit.RetrofitHelper
import com.example.nbrbcurrency.retrofit.models.CurrencyData
import com.example.nbrbcurrency.retrofit.models.CurrencyDataList
import com.example.nbrbcurrency.utils.DateHelper
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.core.Single.*
import io.reactivex.rxjava3.functions.BiFunction
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.Retrofit
import java.util.*

class CurrencyViewModel : ViewModel() {
    fun getCurrencyData(): @NonNull Single<Array<List<CurrencyData>>>? {
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

        return coursesData?.observeOn(Schedulers.newThread())
    }
}