package com.example.betterthanyesterday.Viewmodel

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterthanyesterday.R
import com.example.betterthanyesterday.View.Todo.TodoAdapter
import com.example.betterthanyesterday.databinding.FragmentTodoBinding

class TodoFragment : Fragment() {

    val viewModel: TodoViewModel by activityViewModels()

    var binding: FragmentTodoBinding? = null

    // 뷰를 만들 때 실행할 내용
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoBinding.inflate(inflater)
        return binding?.root
    }

    // 뷰가 만들어지면 실행할 내용
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TodoAdapter(emptyArray(), parentFragmentManager, viewModel)
        binding?.recTodos?.adapter = adapter
        binding?.recTodos?.layoutManager = LinearLayoutManager(requireContext())

        // 뷰모델의 LiveData 구독
        viewModel.todoList.observe(viewLifecycleOwner) { todos ->
            adapter.updateData(todos.toTypedArray())
        }

        binding?.btnAdd?.setOnClickListener {
            findNavController().navigate(R.id.action_todoFragment_to_todoAddFragment)
        }

    }

    // 뷰 파괴될 때 메모리 누수 방지
    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}