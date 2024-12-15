package com.example.betterthanyesterday.View.Todo

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.betterthanyesterday.R
import com.example.betterthanyesterday.databinding.ListTodosBinding
import com.example.betterthanyesterday.Viewmodel.Todo
import com.example.betterthanyesterday.Viewmodel.TodoViewModel

class TodoAdapter(private var todos: Array<Todo>,
                  private val parentFragmentManager: FragmentManager, // 다이얼로그 호출
                  private val viewModel: TodoViewModel)
    : RecyclerView.Adapter<TodoAdapter.Holder>() {

    fun updateData(newTodos: Array<Todo>) {
        todos = newTodos
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ListTodosBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding, parentFragmentManager, viewModel)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(todos[position])
    }

    override fun getItemCount() = todos.size

    class Holder(
        private val binding: ListTodosBinding,
        private val parentFragmentManager: FragmentManager,
        private val viewModel: TodoViewModel)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(todo: Todo) {
            binding.txtTitle.text = todo.title
            binding.imageEdit.setImageResource(R.drawable.todo_edit)
            binding.imageDelete.setImageResource(R.drawable.todo_delete)
            binding.imageCheck.setImageResource(R.drawable.todo_check)

            binding.imageEdit.setOnClickListener {
                val todoDialogFragment = TodoDialogFragment.newInstance(todo.title, todo.detail)
                todoDialogFragment.show(parentFragmentManager, "TodoDialog")
            }

            // 삭제 버튼 클릭 리스너
            binding.imageDelete.setOnClickListener {
                val deleteSuccess = MutableLiveData<Boolean>()
                // Todo 삭제
                viewModel.deleteTodo(todo, deleteSuccess)

                // 삭제 완료 후 처리
                deleteSuccess.observeForever { success ->
                    if (success) {
                        // 삭제 성공 시 로컬 리스트에서 해당 Todo 제거
                        viewModel.removeTodo(todo)
                    } else {
                        // 삭제 실패 시 사용자에게 알림
                        Toast.makeText(binding.root.context, "Todo 삭제 실패", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            // Check 버튼 클릭 리스너
            binding.imageCheck.setOnClickListener {
                val currentDrawable = binding.imageCheck.drawable.constantState
                val checkDrawable = binding.root.context.getDrawable(R.drawable.todo_check)?.constantState
                val checkDoneDrawable = binding.root.context.getDrawable(R.drawable.todo_check_done)?.constantState

                if (currentDrawable == checkDrawable) {
                    // 이미지가 todo_check일 때
                    binding.imageCheck.setImageResource(R.drawable.todo_check_done)
                } else if (currentDrawable == checkDoneDrawable) {
                    // 이미지가 todo_check_done일 때
                    binding.imageCheck.setImageResource(R.drawable.todo_check)
                }
            }
        }
    }
}