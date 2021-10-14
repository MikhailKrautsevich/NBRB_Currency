package com.example.nbrbcurrency

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.nbrbcurrency.retrofit.CurrencyApi
import com.example.nbrbcurrency.retrofit.RetrofitHelper
import com.example.nbrbcurrency.retrofit.models.BreakFastMenu
import com.example.nbrbcurrency.retrofit.models.CurrencyDataList
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity() {

    companion object {
        private const val LOG = "LOG"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit: Retrofit = RetrofitHelper.getRetrofit()
        val api: CurrencyApi? = RetrofitHelper.getApi(retrofit)

        val call = api?.getCurrencyList("2021.10.1")
        val testCall = api?.test()

        call?.enqueue(object : Callback<CurrencyDataList> {
            override fun onResponse(
                call: Call<CurrencyDataList>,
                response: Response<CurrencyDataList>
            ) {
                if (response.isSuccessful)
                {Log.d(LOG, "response.body().toString()")}
                else {Log.d(LOG, "Ой!!!!!!!!")}
            }

            override fun onFailure(call: Call<CurrencyDataList>, t: Throwable) {
                Log.d(LOG, t.localizedMessage)
            }
        })

//        testCall?.enqueue(object : Callback<BreakFastMenu>{
//            override fun onResponse(call: Call<BreakFastMenu>, response: Response<BreakFastMenu>) {
//                if (!response.isSuccessful) {
//                    Log.d(LOG, response.body().toString())
//                } else Log.d(LOG, "Ой!!!!!!!!")
//            }
//
//            override fun onFailure(call: Call<BreakFastMenu>, t: Throwable) {
//                Log.d(LOG, t.localizedMessage)
//            }
//        })
    }
}
