package com.example.betterthanyesterday.repository

import com.example.betterthanyesterday.ExerciseRecord
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class WorkoutRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun addExerciseRecord(record: ExerciseRecord) {
        firestore.collection("exerciseRecords").add(record).await()
    }

    suspend fun getExerciseRecords(): List<ExerciseRecord> {
        val snapshot = firestore.collection("exerciseRecords").get().await()
        return snapshot.documents.mapNotNull { it.toObject(ExerciseRecord::class.java) }
    }
}



