package com.venom.venomtasks.services

import com.google.gson.GsonBuilder
import com.venom.venomtasks.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.Retrofit.Builder
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class RetrofitBuilder {
    companion object {
        @Volatile
        private var instance: Retrofit? = null

        fun getRetrofit(): Retrofit {
            if (this.instance == null) {
                createRetrofit()
            }

            return this.instance!!;
        }

        private fun createRetrofit(): Unit {
            createRetrofit("")
        }

        fun createRetrofit(accessToken: String?): Unit {
            val okhttpClientBuilder = OkHttpClient().newBuilder()
            okhttpClientBuilder.readTimeout(10, TimeUnit.SECONDS)
            okhttpClientBuilder.connectTimeout(5, TimeUnit.SECONDS)

            if (!accessToken.isNullOrEmpty()) {
                okhttpClientBuilder.addInterceptor { chain ->
                    val request: Request =
                        chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer $accessToken").build()
                    chain.proceed(request)
                }
            }

            val client: OkHttpClient = okhttpClientBuilder.build()

            this.instance = Builder()
                .baseUrl(BuildConfig.API_URL)
                .addConverterFactory(
                    GsonConverterFactory.create(
                        GsonBuilder().serializeNulls().create()
                    )
                )
                .client(client)
                .build()
        }
    }
}