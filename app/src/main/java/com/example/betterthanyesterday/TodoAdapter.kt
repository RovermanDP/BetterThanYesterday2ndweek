package com.example.betterthanyesterday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.betterthanyesterday.databinding.ListTodosBinding
import com.example.betterthanyesterday.viewmodel.Todo

class TodoAdapter(var todos: Array<Todo>)
    : RecyclerView.Adapter<TodoAdapter.Holder>() {

    fun updateData(newTodos: Array<Todo> ) {
        todos = newTodos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ListTodosBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(todos[position])
    }

    override fun getItemCount() = todos.size

    class Holder(private val binding: ListTodosBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(todo: Todo){
            binding.txtTitle.text = todo.title
            binding.imageEdit.setImageResource(R.drawable.todo_edit)
            binding.imageDelete.setImageResource(R.drawable.todo_delete)
            binding.imageCheck.setImageResource(R.drawable.todo_check)
        }
    }
}

