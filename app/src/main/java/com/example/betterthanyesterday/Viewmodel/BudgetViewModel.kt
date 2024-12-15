package com.example.betterthanyesterday.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.betterthanyesterday.View.Budget.BudgetRecord
import com.example.betterthanyesterday.Repository.BudgetRepository
import kotlinx.coroutines.launch

class BudgetViewModel: ViewModel() {

    private val repository = BudgetRepository()

    private val _budgetRedcords = MutableLiveData<List<BudgetRecord>>()
    val budgetRecords: LiveData<List<BudgetRecord>> get() = _budgetRedcords

    fun addBudgetRecord(date: String, bdrecord: BudgetRecord) {
        viewModelScope.launch {
            val success = repository.addBudgetRecord(date, bdrecord)
            if (success) {
                loadBudgetRecords(date)
            }
        }
    }

    fun loadBudgetRecords(date: String) {
        viewModelScope.launch {
            val records = repository.getBudgetRecords(date)
            _budgetRedcords.value = records
        }
    }
}