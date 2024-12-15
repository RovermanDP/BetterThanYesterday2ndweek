package com.example.betterthanyesterday.Repository

import com.example.betterthanyesterday.View.Budget.BudgetRecord
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class BudgetRepository {

    private val database = FirebaseDatabase.getInstance()
    private val userRef = database.getReference("budget")

    suspend fun addBudgetRecord(date: String, bdrecord: BudgetRecord): Boolean {
        return try {
            val recordRef = userRef.child(date).push()
            recordRef.setValue(bdrecord).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getBudgetRecords(date: String): List<BudgetRecord> {
        return try {
            val snapshot = userRef.child(date).get().await()
            snapshot.children.mapNotNull { it.getValue(BudgetRecord::class.java) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}