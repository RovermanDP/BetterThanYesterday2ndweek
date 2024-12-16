package com.example.betterthanyesterday

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterthanyesterday.databinding.FragmentBudgetAddBinding
import com.example.betterthanyesterday.databinding.FragmentBudgetBinding
import com.example.betterthanyesterday.databinding.FragmentTodoBinding
import com.example.betterthanyesterday.viewmodel.BudgetViewModel


class BudgetAddFragment : Fragment() {

    private val viewModel: BudgetViewModel by activityViewModels()
    private var binding: FragmentBudgetAddBinding? = null
    private var selectedDate: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetAddBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val chooseYear = arguments?.getInt("chooseYear") ?: 0
        val chooseMonth = arguments?.getInt("chooseMonth") ?: 0
        val chooseDay = arguments?.getInt("chooseDay") ?: 0

        selectedDate = "$chooseYear-${chooseMonth.toString().padStart(2, '0')}" + "-${
            chooseDay.toString().padStart(2, '0')
        }"

        binding?.nowyearTxt?.text = chooseYear.toString()
        binding?.nowmonthTxt?.text = chooseMonth.toString().padStart(2, '0')
        binding?.nowdateTxt?.text = chooseDay.toString().padStart(2, '0')

        val adapter = BudgetsAdapter(emptyList()) { record ->
            viewModel.deleteBudgetRecord(selectedDate, record) // 삭제 처리
        }
        binding?.recBudgets?.layoutManager = LinearLayoutManager(requireContext())
        binding?.recBudgets?.adapter = adapter

        // ViewModel 데이터 관찰
        viewModel.budgetRecords.observe(viewLifecycleOwner) { records ->
            adapter.updateRecords(records)
            calculateTotal(records)
        }

        viewModel.loadBudgetRecords(selectedDate)

        binding?.expendBtn?.setOnClickListener {
            val category = binding?.categoryText?.text.toString()
            val amount = binding?.moneyText?.text.toString().toIntOrNull() ?: 0
            val record = BudgetRecord(
                choice = "지출",
                category = category,
                mount = amount,
                year = chooseYear,
                month = chooseMonth,
                day = chooseDay
            )
            viewModel.addBudgetRecord(selectedDate, record)
        }

        binding?.importBtn?.setOnClickListener {
            val category = binding?.categoryText?.text.toString()
            val amount = binding?.moneyText?.text.toString().toIntOrNull() ?: 0
            val record = BudgetRecord(
                choice = "수입",
                category = category,
                mount = amount,
                year = chooseYear,
                month = chooseMonth,
                day = chooseDay
            )
            viewModel.addBudgetRecord(selectedDate, record)
        }
    }

    private fun calculateTotal(records: List<BudgetRecord>) {
        var total = 0

        for (record in records) {
            val amount = record.mount

            if (record.choice == "수입") {
                total += amount
            } else if (record.choice == "지출") {
                total -= amount
            }
        }
        binding?.summoneyTxt?.text = total.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}