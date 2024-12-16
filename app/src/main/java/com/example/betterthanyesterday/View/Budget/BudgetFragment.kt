package com.example.betterthanyesterday

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterthanyesterday.databinding.FragmentBudgetBinding
import com.example.betterthanyesterday.databinding.FragmentTodoBinding
import com.example.betterthanyesterday.viewmodel.BudgetViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class BudgetFragment : Fragment() {

    var binding: FragmentBudgetBinding? = null
    private val budgetViewModel: BudgetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBudgetBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 현재 월 및 연도 계산
        val currentDate = java.util.Calendar.getInstance()
        val currentYear = currentDate.get(java.util.Calendar.YEAR)
        val currentMonth = currentDate.get(java.util.Calendar.MONTH) + 1

        // CalendarView 클릭 시 날짜 선택하여 다른 프래그먼트로 이동
        binding?.calendar?.setOnDateChangeListener { _, year, month, dayOfMonth ->  //람다함수
            val chooseYear = year
            val chooseMonth = month + 1
            val chooseDay = dayOfMonth

            val bundle = Bundle().apply {
                //putInt(Bundle에 데이터 저장하는 메서드)로 날짜 bundle에 저장
                putInt("chooseYear", chooseYear)
                putInt("chooseMonth", chooseMonth)
                putInt("chooseDay", chooseDay)
            }

            findNavController().navigate(R.id.budgetAddFragment, bundle)
        }


        //월별 지출 합계 로드 -> 월 지출 총액에 이용
        budgetViewModel.loadMonthlyExpenseSum(currentYear, currentMonth)

        // 연 지출 합계 로드 -> 그래프에 이용
        budgetViewModel.loadYearlyExpenseSum(currentYear)

        // 월별 지출 합계 관찰
        budgetViewModel.yearlyExpenseSum.observe(viewLifecycleOwner) { monthlySums ->
            setupLineChart(monthlySums)
        }

        // LiveData를 관찰해 real_txt에 지출 총액 반영
        budgetViewModel.monthlyExpenseSum.observe(viewLifecycleOwner) { totalExpense ->
            binding?.realTxt?.text = totalExpense.toString()
            budgetViewModel.calculatePercentSpent() // 퍼센트 계산
        }

        // 목표 금액 관찰
        budgetViewModel.monthlyGoal.observe(viewLifecycleOwner) { goal ->
            binding?.goalTxt?.text = goal.toString()
            budgetViewModel.calculatePercentSpent() // 목표 금액 변경 시 퍼센트 계산
        }

        // 퍼센트 값과 ProgressBar 업데이트
        budgetViewModel.percentSpent.observe(viewLifecycleOwner) { percent ->
            binding?.percentTxt?.text = "$percent%"
            binding?.progressBar?.progress = percent
        }

        // 목표 지출 입력 시 반영
        binding?.goalBtn?.setOnClickListener {
            showGoalInputDialog()
        }
    }


    private fun showGoalInputDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("목표 지출 설정")    //다이얼로그 title설정

        // 입력창 추가
        val input = EditText(requireContext()).apply {
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }
        builder.setView(input)

        // 확인 버튼 클릭 시 목표 금액 설정
        builder.setPositiveButton("확인") { dialog, _ ->
            val goalText = input.text.toString()
            val goalValue = goalText.toDoubleOrNull()
            if (goalValue != null) {
                budgetViewModel.setMonthlyGoal(goalValue)
                binding?.goalTxt?.text = goalText
            } else {
                Toast.makeText(requireContext(), "유효한 숫자를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        // 취소 버튼
        builder.setNegativeButton("취소") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    //linechart 설정
    private fun setupLineChart(monthlySums: List<Int>) {
        // X축의 레이블 설정 (1월부터 12월)
        val months = (1..12).map { "${it}월" }

        // LineDataSet 만들기
        val entries = monthlySums.mapIndexed { index, sum ->
            com.github.mikephil.charting.data.Entry(index.toFloat(), sum.toFloat()) // (x, y) 좌표
        }

        val dataSet = LineDataSet(entries, "월별 지출") // 데이터셋에 레이블 설정
        dataSet.color = resources.getColor(R.color.action_bar_color, null) // 선 색 설정
        dataSet.valueTextColor = resources.getColor(R.color.black, null) // 값 색 설정
        dataSet.valueTextSize = 10f

        // LineData로 LineChart 설정
        val lineData = com.github.mikephil.charting.data.LineData(dataSet) // LineData 객체로 감싸기
        binding?.chart?.data = lineData // chart에 LineData 객체 설정

        // X축 설정 (1~12월)
        binding?.chart?.xAxis?.apply {
            valueFormatter = IndexAxisValueFormatter(months) // 월 레이블 설정
            position = XAxis.XAxisPosition.BOTTOM //x축 레이블 위치
            setDrawGridLines(false) // 그리드 선 없애기
        }

        // Y축 설정
        binding?.chart?.axisLeft?.apply {
            setDrawGridLines(true) // 그리드 선 보이기
            setDrawAxisLine(true) // Y축선 보이기
        }

        binding?.chart?.axisRight?.isEnabled = false // 오른쪽 Y축 비활성화

        // 차트 업데이트
        binding?.chart?.invalidate()
    }

}