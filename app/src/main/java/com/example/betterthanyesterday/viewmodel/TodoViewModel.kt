package com.example.betterthanyesterday.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.betterthanyesterday.Repository.TodoRepository

data class Todo(
    val title: String = "",
    val detail: String? = null
)

class TodoViewModel: ViewModel() {
    private val _todoList = MutableLiveData<MutableList<Todo>>(mutableListOf())
    val todoList: LiveData<MutableList<Todo>> get() = _todoList

    private val repository = TodoRepository()
    init {
        repository.observeTodo(_todoList)
    }

    fun addTodo(todo: Todo) {
        repository.postTodo(todo)
    }
}