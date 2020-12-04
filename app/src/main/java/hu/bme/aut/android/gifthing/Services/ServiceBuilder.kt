package hu.bme.aut.android.gifthing.services

import hu.bme.aut.android.gifthing.AppPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object ServiceBuilder {
    private const val URL = "http://192.168.1.84:8080/api/"

    fun <T> buildService(serviceType: Class<T>): T {
        val okHttpClient = OkHttpClient.Builder()
        val headerInterceptor = HeaderInterceptor()
        okHttpClient.addInterceptor(headerInterceptor)
        okHttpClient.callTimeout(1, TimeUnit.SECONDS)
        okHttpClient.connectTimeout(1, TimeUnit.SECONDS)
        okHttpClient.writeTimeout(1, TimeUnit.SECONDS)
        okHttpClient.readTimeout(1, TimeUnit.SECONDS)

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
        if (token != null) {
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