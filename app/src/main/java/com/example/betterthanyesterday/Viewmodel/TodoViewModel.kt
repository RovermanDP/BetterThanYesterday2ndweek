package com.example.betterthanyesterday.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.betterthanyesterday.Repository.TodoRepository

data class Todo(
    val title: String = "",
    val detail: String = "",
    val imgUri: String? = null,
)


class TodoViewModel : ViewModel() {
    private val _todoList = MutableLiveData<MutableList<Todo>>(mutableListOf())
    val todoList: LiveData<MutableList<Todo>> get() = _todoList

    // 업데이트 성공 여부 LiveData
    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> get() = _updateSuccess

    private val repository = TodoRepository()

    init {
        repository.observeTodo(_todoList)
    }

    fun addTodo(todo: Todo) {
        repository.postTodo(todo, _updateSuccess)
    }

    fun updateTodo(updatedTodo: Todo) {
        repository.updateTodo(updatedTodo, _updateSuccess)
        _updateSuccess.observeForever { success ->
            if (success) {
                val currentList = _todoList.value ?: mutableListOf()
                val updatedList = currentList.map { todo ->
                    if (todo.title == updatedTodo.title) updatedTodo else todo
                }.toMutableList()
                _todoList.value = updatedList
            }
        }
    }


    fun deleteTodo(todo: Todo) {
        repository.deleteTodo(todo)
    }

    // 삭제 성공 시 Todo를 로컬 리스트에서 제거
    fun removeTodo(todo: Todo) {
        val currentList = _todoList.value ?: mutableListOf()
        val updatedList = currentList.filter { it != todo }.toMutableList()
        _todoList.value = updatedList
    }
}