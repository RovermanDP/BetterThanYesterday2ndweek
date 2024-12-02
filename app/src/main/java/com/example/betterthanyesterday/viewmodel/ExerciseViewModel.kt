package com.example.betterthanyesterday

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import data.Model.ExerciseRecord

class ExerciseViewModel : ViewModel() {

    private val _exerciseRecords = MutableLiveData<List<ExerciseRecord>>()
    val exerciseRecords: LiveData<List<ExerciseRecord>> get() = _exerciseRecords

    private val firestore = FirebaseFirestore.getInstance()

    // 날짜별 운동 기록 불러오기
    fun loadExerciseRecords(date: String) {
        firestore.collection("exercise_records")
            .document(date)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val records = document.get("records") as? List<Map<String, Any>> ?: emptyList()
                    _exerciseRecords.value = records.map { map ->
                        ExerciseRecord(
                            type = map["type"] as String,
                            caloriesBurned = (map["caloriesBurned"] as Long).toInt()
                        )
                    }
                } else {
                    _exerciseRecords.value = emptyList()
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to load records", e)
                _exerciseRecords.value = emptyList()
            }
    }

    // 날짜별 운동 기록 추가
    fun addExerciseRecord(date: String, record: ExerciseRecord) {
        val recordMap = mapOf(
            "type" to record.type,
            "caloriesBurned" to record.caloriesBurned
        )
        firestore.collection("exercise_records")
            .document(date)
            .update("records", FieldValue.arrayUnion(recordMap))
            .addOnSuccessListener {
                loadExerciseRecords(date) // 최신 데이터 불러오기
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to add record", e)
                // 문서가 없으면 생성 후 다시 추가
                firestore.collection("exercise_records")
                    .document(date)
                    .set(mapOf("records" to listOf(recordMap)))
                    .addOnSuccessListener { loadExerciseRecords(date) }
            }
    }
}