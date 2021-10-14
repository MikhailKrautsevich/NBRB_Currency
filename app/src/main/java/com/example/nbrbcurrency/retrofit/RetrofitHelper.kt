package com.example.nbrbcurrency.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class RetrofitHelper {
    companion object {
        private const val BASE_URL = "https://www.nbrb.by/Services/"
        private const val TEST_URL = "http://www.w3schools.com"

        fun getRetrofit(): Retrofit {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build()
        }

        fun getApi(retrofit: Retrofit?): CurrencyApi? = retrofit?.create(CurrencyApi::class.java)
    }
}