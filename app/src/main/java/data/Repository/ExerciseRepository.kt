package com.example.betterthanyesterday.repository

import com.google.firebase.firestore.FirebaseFirestore
import data.Model.ExerciseRecord
import kotlinx.coroutines.tasks.await

class ExerciseRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun addExerciseRecord(record: ExerciseRecord) {
        firestore.collection("exerciseRecords").add(record).await()
    }

    suspend fun getExerciseRecords(): List<ExerciseRecord> {
        val snapshot = firestore.collection("exerciseRecords").get().await()
        return snapshot.documents.mapNotNull { it.toObject(ExerciseRecord::class.java) }
    }
}



