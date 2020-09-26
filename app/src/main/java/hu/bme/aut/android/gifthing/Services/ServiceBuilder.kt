package hu.bme.aut.android.gifthing.services

import hu.bme.aut.android.gifthing.AppPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ServiceBuilder {
    private const val URL = "http://192.168.0.106:8080/api/"

    private val okHttpClient = OkHttpClient.Builder()



    fun <T> buildService(serviceType: Class<T>) : T {
        val headerInterceptor = HeaderInterceptor()
        okHttpClient.addInterceptor(headerInterceptor)

        return Retrofit.Builder().baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient.build())
            .build()
            .create(serviceType)
    }
}

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        val token = AppPreferences.token
        if(token != null) {
            proceed(
                request()
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            )
        } else {
            proceed(
                request()
                    .newBuilder()
                    .build()
            )
        }
    }
}