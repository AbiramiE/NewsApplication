package com.newsapplication

import com.newsapplication.api.RetrofitService
import javax.inject.Inject

class NewsRepository @Inject constructor(private val retrofitService: RetrofitService) {

    suspend fun getAllNewsDetails() = retrofitService.getAllNews()

}