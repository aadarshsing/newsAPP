package com.example.newsly

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//https://newsapi.org/v2/top-headlines?country=us&apiKey=8369e00f05504a688d484a4993e3ca08
//https://newsapi.org/v2/everything?q=apple&from=2024-12-29&to=2024-12-29&sortBy=popularity&apiKey=8369e00f05504a688d484a4993e3ca08
const val  BASE_URL = "https://newsapi.org"
const val  API_KEY = "2340ac617c5c4c049418042eb624627b"
interface NewsInterface {
    @GET("/v2/top-headlines?apiKey=$API_KEY")
    fun getHeadlines(@Query("country") country : String,@Query("page") page :Int) : Call<News>

    //this is how new url built -> https://newsapi.org/v2/top-headlines?apiKey=$API_KEY&country=us&page=10

    object NewsService {
        private val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "Android-Newsly-App")
                    .build()
                chain.proceed(request)
            }
            .build()

        val newsInstance: NewsInterface by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NewsInterface::class.java)
        }
    }
}
