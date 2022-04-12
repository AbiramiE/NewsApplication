package com.newsapplication.api

import com.newsapplication.model.NewsBO
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface RetrofitService {

    @GET("top-headlines?country=in&category=business&apiKey=4e045681e9c24eff82bcad124c84a59e")
    suspend fun getAllNews() : Response<NewsBO>

}