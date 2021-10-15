package com.example.nbrbcurrency.retrofit

import com.example.nbrbcurrency.retrofit.models.CurrencyDataList
import io.reactivex.rxjava3.core.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {

    @GET(value = "XmlExRates.aspx?periodicity=0")
    fun getCurrencyList(@Query("ondate") ondate: String): Single<CurrencyDataList>
}