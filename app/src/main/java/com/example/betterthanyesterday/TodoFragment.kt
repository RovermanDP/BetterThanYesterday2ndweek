package com.example.betterthanyesterday

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterthanyesterday.databinding.FragmentTodoBinding
import com.example.betterthanyesterday.viewmodel.Todo
import com.example.betterthanyesterday.viewmodel.TodoViewModel

class TodoFragment : Fragment() {

    val viewModel: TodoViewModel by activityViewModels()

    var binding: FragmentTodoBinding? = null
    val todos = arrayOf(
        Todo("객프 강의 듣기"),
        Todo("BTY 만들기"),
        Todo("자기소개서 쓰기"),
        Todo("데이트 하기"),
        Todo("후쿠오카 여행 계획 짜기"),
        Todo("컴구 강의 듣기")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTodoBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = TodoAdapter(emptyArray())
        binding?.recTodos?.adapter = adapter
        binding?.recTodos?.layoutManager = LinearLayoutManager(requireContext())

        viewModel.todoList.observe(viewLifecycleOwner) { todos ->
            adapter.updateData(todos.toTypedArray())
        }

        binding?.btnAdd?.setOnClickListener {
            findNavController().navigate(R.id.action_todoFragment_to_todoAddFragment)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}