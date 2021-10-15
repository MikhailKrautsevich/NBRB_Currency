package com.example.nbrbcurrency

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.nbrbcurrency.retrofit.CurrencyApi
import com.example.nbrbcurrency.retrofit.RetrofitHelper
import com.example.nbrbcurrency.retrofit.models.CurrencyDataList
import com.example.nbrbcurrency.utils.DateHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOG = "_LOG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit: Retrofit = RetrofitHelper.getRetrofit()
        val api: CurrencyApi? = RetrofitHelper.getApi(retrofit)
        val call = api?.getCurrencyList("2021.10.15")

        call?.enqueue(object : Callback<CurrencyDataList> {
            override fun onResponse(
                call: Call<CurrencyDataList>,
                response: Response<CurrencyDataList>
            ) {
                Log.d(LOG, response.body()?.currencies?.size.toString())
            }

            override fun onFailure(call: Call<CurrencyDataList>, t: Throwable) {
                Log.d(LOG, "" + t.localizedMessage)
            }
        })

        val date: Date = Date()
        Log.d(LOG, DateHelper.getFormattedDate(date))
        Log.d(LOG, DateHelper.getTomorrowDate(date))
    }
}
