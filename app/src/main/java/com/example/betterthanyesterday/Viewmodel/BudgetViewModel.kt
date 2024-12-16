package com.example.betterthanyesterday.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.betterthanyesterday.BudgetRecord
import com.example.betterthanyesterday.Repository.BudgetRepository
import kotlinx.coroutines.launch



class BudgetViewModel: ViewModel() {

    private val repository = BudgetRepository()
    private val _budgetRecords = MutableLiveData<List<BudgetRecord>>()
    val budgetRecords: LiveData<List<BudgetRecord>> get() = _budgetRecords

    // 가계 추가
    fun addBudgetRecord(date: String, bdrecord: BudgetRecord) {
        viewModelScope.launch {
            val success = repository.addBudgetRecord(date, bdrecord)
            if (success) {
                loadBudgetRecords(date)
            }
        }
    }

    //가계 제거
    fun deleteBudgetRecord(date: String, record: BudgetRecord) {
        viewModelScope.launch {
            val success = repository.deleteBudgetRecord(date, record)
            if (success) {
                loadBudgetRecords(date) // 삭제 후 데이터 갱신
            }
        }
    }

    //가계 로드
    fun loadBudgetRecords(date: String) {
        viewModelScope.launch {
            val records = repository.getBudgetRecords(date)
            _budgetRecords.value = records
        }
    }

    //월 지출 합계
    private val _monthlyExpenseSum = MutableLiveData<Int>()
    val monthlyExpenseSum: LiveData<Int> get() = _monthlyExpenseSum

    fun loadMonthlyExpenseSum(year: Int, month: Int) {
        viewModelScope.launch {
            val totalExpense = repository.getExpenseSum(year, month)
            _monthlyExpenseSum.value = totalExpense
        }
    }

    //연 지출 현황
    private val _yearlyExpenseSum = MutableLiveData<List<Int>>()
    val yearlyExpenseSum: LiveData<List<Int>> get() = _yearlyExpenseSum

    fun loadYearlyExpenseSum(year: Int) {
        viewModelScope.launch {
            val monthlySums = mutableListOf<Int>()
            for (month in 1..12) {
                val totalExpense = repository.getExpenseSum(year, month)
                monthlySums.add(totalExpense)
            }
            _yearlyExpenseSum.value = monthlySums
        }
    }

    // 월 목표 지출 설정
    private val _monthlyGoal = MutableLiveData<Double>()
    val monthlyGoal: LiveData<Double> get() = _monthlyGoal

    fun setMonthlyGoal(goal: Double) {
        _monthlyGoal.value = goal
    }

    // 퍼센트 구하기
    private val _percentSpent = MutableLiveData<Int>()
    val percentSpent: LiveData<Int> get() = _percentSpent

    fun calculatePercentSpent() {
        val totalExpense = _monthlyExpenseSum.value ?: 0
        val goal = _monthlyGoal.value ?: 0.0

        if (goal > 0) {
            val percent = ((totalExpense / goal) * 100).toInt()
            _percentSpent.value = percent
        } else {
            _percentSpent.value = 0
        }
    }
}