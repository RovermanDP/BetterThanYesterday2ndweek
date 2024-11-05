package com.example.betterthanyesterday

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterthanyesterday.databinding.FragmentBudgetAddBinding
import com.example.betterthanyesterday.databinding.FragmentBudgetBinding
import com.example.betterthanyesterday.databinding.FragmentTodoBinding


class BudgetAddFragment : Fragment() {

    var binding: FragmentBudgetAddBinding? = null

    val budgets = arrayOf(
        Budget("지출","점심", 4500),
        Budget("지출","커피", 5000),
        Budget("지출","저녁", 5000),
        Budget("지출","PC방", 5000),
        Budget("지출","생일선물", 5000),
        Budget("지출","버스비", 5000)
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetAddBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

       binding?.recExpends?.layoutManager = LinearLayoutManager(requireContext())
        binding?.recExpends?.adapter = BudgetsAdapter(budgets)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}