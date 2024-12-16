package com.example.betterthanyesterday.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.betterthanyesterday.Repository.ExerciseRepository
import com.example.betterthanyesterday.data.RemoteLocation
import kotlinx.coroutines.launch

class LocationViewModel (private val exerciseRepository: ExerciseRepository) : ViewModel(){

    private val _searchResult = MutableLiveData<SearchResultDataState>()
    val searchResult: LiveData<SearchResultDataState> get()= _searchResult

    fun searchLocation(query: String){
        viewModelScope.launch {
            emitSearchResultUiState(isLoading = true)
            val searchResult = exerciseRepository.searchLocation(query)
            if(searchResult.isNullOrEmpty()){
                emitSearchResultUiState(error = "Location not found, please try again")
            }else{
                emitSearchResultUiState(locations = searchResult)
            }
        }
    }

    private fun emitSearchResultUiState(
        isLoading: Boolean = false,
        locations: List<RemoteLocation>? = null,
        error: String? = null

    ){
        val searchResultDataState = SearchResultDataState(isLoading, locations, error)
        _searchResult.value = searchResultDataState

    }

    data class SearchResultDataState(
        val isLoading: Boolean ,
        val locations: List<RemoteLocation>? ,
        val error: String?
    )
}