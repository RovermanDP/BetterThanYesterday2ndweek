package com.example.betterthanyesterday.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.betterthanyesterday.viewmodel.Todo
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class TodoRepository {
    val database = Firebase.database
    val todoRef = database.getReference("todo")

    fun observeTodo(todo: MutableLiveData<MutableList<Todo>>) {
        todoRef.addValueEventListener(object: ValueEventListener {
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

    fun postTodo(todo: Todo) {
        val newKey = todoRef.push().key
        if (newKey != null) {
            todoRef.child(newKey).setValue(todo)
        }
    }
}
