package com.example.betterthanyesterday.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.betterthanyesterday.Viewmodel.Todo
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot

class TodoRepository {
    private val database = FirebaseDatabase.getInstance()
    private val todoRef = database.getReference("todo")

    // Todo 목록을 실시간으로 가져오는 함수
    fun observeTodo(todo: MutableLiveData<MutableList<Todo>>) {
        todoRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val todos = mutableListOf<Todo>()
                for (todoSnapshot in snapshot.children) {
                    val todoItem = todoSnapshot.getValue(Todo::class.java)
                    todoItem?.let { todos.add(it) }
                }
                todo.postValue(todos)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TodoRepository", "Database error: ${error.message}")
            }
        })
    }

    // Todo 추가 함수, 성공 여부를 LiveData로 처리
    fun postTodo(todo: Todo, onComplete: MutableLiveData<Boolean>) {
        val newTodoRef = todoRef.push()
        newTodoRef.setValue(todo).addOnCompleteListener { task ->
            onComplete.postValue(task.isSuccessful)  // 성공 여부를 LiveData에 반영
        }
    }

    // Todo 업데이트 함수 (title과 detail을 기준으로 업데이트)
    fun updateTodo(updatedTodo: Todo, onComplete: MutableLiveData<Boolean>) {
        todoRef.orderByChild("title").equalTo(updatedTodo.title).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (todoSnapshot in snapshot.children) {
                    val todo = todoSnapshot.getValue(Todo::class.java)
                    if (todo != null && todo.title == updatedTodo.title && todo.detail == updatedTodo.detail) {
                        todoSnapshot.ref.setValue(updatedTodo).addOnCompleteListener { task ->
                            onComplete.postValue(task.isSuccessful)
                        }
                        return
                    }
                }
                onComplete.postValue(false)  // 업데이트 실패
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TodoRepository", "Database error: ${error.message}")
                onComplete.postValue(false)
            }
        })
    }

    // Todo 삭제 함수
    fun deleteTodo(todo: Todo, onComplete: MutableLiveData<Boolean>) {
        todoRef.orderByChild("title").equalTo(todo.title)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (todoSnapshot in snapshot.children) {
                        val existingTodo = todoSnapshot.getValue(Todo::class.java)
                        if (existingTodo != null && existingTodo.title == todo.title && existingTodo.detail == todo.detail) {
                            todoSnapshot.ref.removeValue().addOnCompleteListener { task ->
                                onComplete.postValue(task.isSuccessful)
                            }
                            return
                        }
                    }
                    onComplete.postValue(false) // Todo를 찾지 못했거나 삭제 실패
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TodoRepository", "Database error: ${error.message}")
                    onComplete.postValue(false)
                }
            })
    }
}

