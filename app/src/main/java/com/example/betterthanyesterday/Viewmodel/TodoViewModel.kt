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

    // Todo 추가
    fun addTodo(todo: Todo) {
        repository.postTodo(todo, _updateSuccess)
    }

    // Todo 수정
    fun updateTodo(updatedTodo: Todo) {
        repository.updateTodo(updatedTodo, _updateSuccess)
        // Todo 로컬 리스트 수정
        _updateSuccess.observeForever { success ->
            if (success) {
                val currentList = _todoList.value ?: mutableListOf()
                val updatedList = currentList.map { todo ->
                    // 제목 기준으로 일치하는 항목 찾음
                    if (todo.title == updatedTodo.title) updatedTodo else todo
                }.toMutableList()
                _todoList.value = updatedList
            }
        }
    }

    // Todo 삭제
    fun deleteTodo(todo: Todo) {
        repository.deleteTodo(todo)
    }

    // Todo 로컬 리스트 삭제
    fun removeTodo(todo: Todo) {
        val currentList = _todoList.value ?: mutableListOf()
        val updatedList = currentList.filter { it != todo }.toMutableList()
        _todoList.value = updatedList
    }
}