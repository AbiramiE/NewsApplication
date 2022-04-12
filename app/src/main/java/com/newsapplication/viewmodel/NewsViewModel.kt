package com.newsapplication.viewmodel

import android.os.Parcel
import android.os.Parcelable
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.newsapplication.NewsRepository
import com.newsapplication.di.MyPreference
import com.newsapplication.model.NewsBO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(private val mainRepository: NewsRepository,
                                        private val myPreference: MyPreference) : ViewModel(){

    val errorMessage = MutableLiveData<String>()
    val _newsList = MutableLiveData<NewsBO>()
    val _favoriteList = MutableLiveData<Boolean>()
    var job: Job? = null

    val loading = MutableLiveData<Boolean>()

    val newsList : LiveData<NewsBO>
        get() = _newsList

    val favoriteNewsList : LiveData<Boolean>
        get() = _favoriteList

    fun getAllNewsList() {
        loading.value = true
        viewModelScope.launch {
            val response = mainRepository.getAllNewsDetails()
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    _newsList.postValue(response.body())
                    loading.value = false
                } else {
                    _newsList.postValue(null)
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }

    fun setFavorite(id: String, isFavorite: Boolean){
        myPreference.setStoredTag(id, isFavorite)
        _favoriteList.postValue(isFavorite)
    }

    fun getFavorite(id: String) : Boolean {
        _favoriteList.postValue(myPreference.getStoredTag(id))
        return myPreference.getStoredTag(id)
    }

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        //job?.cancel()
    }

}