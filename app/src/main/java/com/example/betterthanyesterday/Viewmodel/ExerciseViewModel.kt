package com.example.betterthanyesterday

import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.betterthanyesterday.Repository.ExerciseRepository
import com.example.betterthanyesterday.View.Exercise.ExerciseRecord
import com.example.betterthanyesterday.data.CurrentLocation
import com.example.betterthanyesterday.data.CurrentWeather
import com.example.betterthanyesterday.data.LiveDataEvent
import com.example.betterthanyesterday.data.WeatherData
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch

class ExerciseViewModel(private val repository: ExerciseRepository) : ViewModel() {

    private val _exerciseRecords = MutableLiveData<List<ExerciseRecord>>()
    val exerciseRecords: LiveData<List<ExerciseRecord>> get() = _exerciseRecords

    // 운동 기록 가져오기
    fun loadExerciseRecords(date: String) {
        viewModelScope.launch {
            try {
                val records = repository.getExerciseRecords(date)
                _exerciseRecords.postValue(records)
            } catch (e: Exception) {
                Log.e("Firebase", "Failed to load records", e)
                _exerciseRecords.postValue(emptyList())
            }
        }
    }

    // 운동 기록 추가
    fun addExerciseRecord(date: String, record: ExerciseRecord) {
        viewModelScope.launch {
            try {
                val success = repository.addExerciseRecord(date, record)
                if (success) {
                    loadExerciseRecords(date) // 최신 데이터 다시 불러오기
                }
            } catch (e: Exception) {
                Log.e("Firebase", "Failed to add record", e)
            }
        }
    }

    private val _currentLocation = MutableLiveData<LiveDataEvent<CurrentLocationDataState>>()
    val currentLocation: LiveData<LiveDataEvent<CurrentLocationDataState>> get() = _currentLocation

    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        geocoder: Geocoder
    ) {
        viewModelScope.launch {
            emitCurrentLocationUiState(isLoading = true)
            repository.getCurrentLocation(
                fusedLocationProviderClient = fusedLocationProviderClient,
                onSuccess = { location ->
                    updateAddressText(location, geocoder)
                },
                onFailure = {
                    emitCurrentLocationUiState(error = "Failed to get current location")
                }
            )
        }
    }

    private fun updateAddressText(currentLocation: CurrentLocation, geocoder: Geocoder) {
        viewModelScope.launch {
            runCatching {
                repository.updateAddressText(currentLocation, geocoder) // Repository의 함수 호출
            }.onSuccess { updatedLocation ->
                emitCurrentLocationUiState(currentLocation = updatedLocation)
            }.onFailure {
                emitCurrentLocationUiState(
                    currentLocation = currentLocation.copy(location = "N/A"),
                    error = "Failed to update address"
                )
            }
        }
    }


    private fun emitCurrentLocationUiState(
        isLoading: Boolean = false,
        currentLocation: CurrentLocation? = null,
        error: String? = null
    ) {
        val currentLocationDataState = CurrentLocationDataState(isLoading, currentLocation, error)
        _currentLocation.value = LiveDataEvent(currentLocationDataState)
    }

    data class CurrentLocationDataState(
        val isLoading: Boolean,
        val currentLocation: CurrentLocation?,
        val error: String?
    )

    private val _weatherData = MutableLiveData<LiveDataEvent<WeatherDataState>>()
    val weatherData: LiveData<LiveDataEvent<WeatherDataState>> get() = _weatherData

    fun getWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            emitWeatherDataUiState(isLoading = true)
            repository.getWeatherData(latitude, longitude)?.let { weatherData ->
                emitWeatherDataUiState(
                    currentWeather = CurrentWeather(
                        icon = weatherData.current.condition.icon,
                        temperature = weatherData.current.temperature,
                        wind = weatherData.current.wind,
                        humidity = weatherData.current.humidity,
                        chanceOfRain = weatherData.forecast.forecastDay.first().day.chanceOfRain
                    )
                )
            } ?: emitWeatherDataUiState(error = "Failed to get weather data")
        }
    }

    private fun emitWeatherDataUiState(
        isLoading: Boolean = false,
        currentWeather: CurrentWeather? = null,
        error: String? = null
    ) {
        val weatherDataState = WeatherDataState(isLoading, currentWeather, error)
        _weatherData.value = LiveDataEvent(weatherDataState)
    }

    data class WeatherDataState(
        val isLoading: Boolean,
        val currentWeather: CurrentWeather?,
        val error: String?
    )
}


