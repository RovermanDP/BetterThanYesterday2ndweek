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

    // Todo 데이터 가져오기
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

    // Todo 추가
    fun postTodo(todo: Todo, onComplete: MutableLiveData<Boolean>) {
        val newTodoRef = todoRef.push()

        newTodoRef.setValue(todo).addOnCompleteListener { task ->
            onComplete.postValue(task.isSuccessful) // 성공하면 LiveData에 알림
        }
    }

    // Todo 수정
    fun updateTodo(updatedTodo: Todo, onComplete: MutableLiveData<Boolean>) {
        todoRef.orderByChild("title").equalTo(updatedTodo.title)
            .addListenerForSingleValueEvent(object : ValueEventListener {

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
                onComplete.postValue(false)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("TodoRepository", "Database error: ${error.message}")
                onComplete.postValue(false)
            }
        })
    }

    // Todo 삭제
    fun deleteTodo(todo: Todo) {
        todoRef.orderByChild("title").equalTo(todo.title)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (todoSnapshot in snapshot.children) {
                        val existingTodo = todoSnapshot.getValue(Todo::class.java)

                        if (existingTodo != null && existingTodo.title == todo.title && existingTodo.detail == todo.detail) {
                            todoSnapshot.ref.removeValue()
                            return
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("TodoRepository", "Database error: ${error.message}")
                }
            })
    }
}