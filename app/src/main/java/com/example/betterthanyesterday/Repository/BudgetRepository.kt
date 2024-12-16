package com.example.betterthanyesterday.Repository

import com.example.betterthanyesterday.BudgetRecord
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class BudgetRepository {

    private val database = FirebaseDatabase.getInstance()
    private val userRef = database.getReference("budget")

    //가계 추가
    suspend fun addBudgetRecord(date: String, bdrecord: BudgetRecord): Boolean {
        return try {
            val year = bdrecord.year.toString()
            val month = bdrecord.month.toString().padStart(2, '0')
            val day = bdrecord.day.toString().padStart(2, '0')

            // 지출 또는 수입을 구분 (bdrecord.choice에 저장된 값 사용)
            val type = bdrecord.choice // "지출" 또는 "수입"

            // 새로운 경로: year -> month -> type(지출/수입) -> day
            val recordRef = userRef.child(year).child(month).child(type).child(day).push()
            recordRef.setValue(bdrecord).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    //가계 제거
    suspend fun deleteBudgetRecord(date: String, record: BudgetRecord): Boolean {
        return try {
            val year = record.year.toString()
            val month = record.month.toString().padStart(2, '0')
            val day = record.day.toString().padStart(2, '0')

            // 삭제할 경로
            val recordRef = userRef
                .child(year)
                .child(month)
                .child(record.choice)
                .child(day)

            // 해당 데이터 탐색 후 삭제
            val snapshot = recordRef.get().await()
            for (child in snapshot.children) {
                val dbRecord = child.getValue(BudgetRecord::class.java)
                if (dbRecord == record) {
                    child.ref.removeValue().await()
                    break
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    //가계 로드
    suspend fun getBudgetRecords(date: String): List<BudgetRecord> {
        return try {
            val dateParts = date.split("-")
            val year = dateParts[0].toIntOrNull() ?: 0
            val month = dateParts[1].toIntOrNull() ?: 0
            val day = dateParts[2].toIntOrNull() ?: 0

            val yearString = year.toString()
            val monthString = month.toString().padStart(2, '0')
            val dayString = day.toString().padStart(2, '0')

            // "지출"과 "수입" 각각의 데이터를 가져옵니다.
            val expenditureSnapshot =
                userRef.child(yearString).child(monthString).child("지출").child(dayString).get()
                    .await()
            val incomeSnapshot =
                userRef.child(yearString).child(monthString).child("수입").child(dayString).get()
                    .await()

            // 두 데이터를 리스트로 변환하고 합칩니다.
            val expenditureRecords =
                expenditureSnapshot.children.mapNotNull { it.getValue(BudgetRecord::class.java) }
            val incomeRecords =
                incomeSnapshot.children.mapNotNull { it.getValue(BudgetRecord::class.java) }

            expenditureRecords + incomeRecords
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    //지출 충합 구하기
    suspend fun getExpenseSum(year: Int, month: Int): Int {
        return try {
            val yearString = year.toString()
            val monthString = month.toString().padStart(2, '0') // 두 자리 수로 맞춤

            val snapshot = userRef.child(yearString).child(monthString).child("지출").get().await()

            // "지출" 하위 모든 금액을 합산
            snapshot.children.flatMap { daySnapshot ->
                daySnapshot.children.mapNotNull { recordSnapshot ->
                    recordSnapshot.getValue(BudgetRecord::class.java)?.mount
                }
            }.sum()
        } catch (e: Exception) {
            e.printStackTrace()
            0 // 오류 발생 시 0 반환
        }
    }
}