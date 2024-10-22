package com.example.pennykeeper.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val _homeData = MutableLiveData<String>()
    val homeData: LiveData<String> = _homeData

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            // Simulate data loading
            _homeData.value = "Welcome to the Home Screen!"
        }
    }

    fun refreshData() {
        loadHomeData()
    }
}