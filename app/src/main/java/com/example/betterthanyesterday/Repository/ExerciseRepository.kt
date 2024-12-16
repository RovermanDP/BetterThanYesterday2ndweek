package com.example.betterthanyesterday.Repository

import android.annotation.SuppressLint
import android.location.Geocoder
import com.example.betterthanyesterday.View.Exercise.ExerciseRecord
import com.example.betterthanyesterday.data.CurrentLocation
import com.example.betterthanyesterday.data.RemoteLocation
import com.example.betterthanyesterday.data.RemoteWeatherData
import com.example.betterthanyesterday.network.api.WeatherAPI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class ExerciseRepository (private val weatherAPI: WeatherAPI){

    // Firebase Database Reference
    private val database = FirebaseDatabase.getInstance()
    private val userRef = database.getReference("exercise_records")

    // 운동 기록 추가
    suspend fun addExerciseRecord(date: String, record: ExerciseRecord): Boolean {
        return try {
            val recordRef = userRef.child(date).push()
            recordRef.setValue(record).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // 특정 날짜의 운동 기록 가져오기
    suspend fun getExerciseRecords(date: String): List<ExerciseRecord> {
        return try {
            val snapshot = userRef.child(date).get().await()
            snapshot.children.mapNotNull { it.getValue(ExerciseRecord::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // [위치 정보 기능]

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(
        fusedLocationProviderClient: FusedLocationProviderClient,
        onSuccess: (currentLocation: CurrentLocation) -> Unit,
        onFailure: () -> Unit
    ) {
        fusedLocationProviderClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            location ?: onFailure()
            onSuccess(
                CurrentLocation(
                    latitude = location.latitude,
                    longitude = location.longitude
                )
            )
        }.addOnFailureListener {
            onFailure()
        }
    }

    @Suppress("DEPRECATION")
    fun updateAddressText(
        currentLocation: CurrentLocation,
        geocoder: Geocoder,
    ): CurrentLocation {
        val latitude = currentLocation.latitude ?: return currentLocation
        val longitude = currentLocation.longitude ?: return currentLocation
        return geocoder.getFromLocation(latitude, longitude, 1)?.let { addresses ->
            val address = addresses[0]
            val addressText = StringBuilder()
            addressText.append(address.locality).append(", ")
            addressText.append(address.adminArea).append(", ")
            addressText.append(address.countryName)
            currentLocation.copy(location = addressText.toString()
            )
        } ?: currentLocation
    }

    suspend fun searchLocation(query: String): List<RemoteLocation>? {
        val response = weatherAPI.searchLocation(query = query)
        return if(response.isSuccessful) response.body() else null
    }

    suspend fun getWeatherData(latitude: Double, longitude: Double): RemoteWeatherData? {
        val response = weatherAPI.getWeatherData(query = "$latitude,$longitude")
        return if (response.isSuccessful) response.body() else null
    }
}




