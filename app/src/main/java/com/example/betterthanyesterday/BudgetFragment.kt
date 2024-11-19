package com.example.betterthanyesterday

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterthanyesterday.databinding.FragmentBudgetBinding
import com.example.betterthanyesterday.databinding.FragmentTodoBinding


class BudgetFragment : Fragment() {

    var binding: FragmentBudgetBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.calendar?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val chooseYear = year
            val chooseMonth = month + 1
            val chooseDay = dayOfMonth

            val bundle = Bundle().apply {
                putInt("chooseYear", chooseYear)
                putInt("chooseMonth", chooseMonth)
                putInt("chooseDay", chooseDay)
            }

            findNavController().navigate(R.id.budgetAddFragment, bundle)
        }
    }
}
