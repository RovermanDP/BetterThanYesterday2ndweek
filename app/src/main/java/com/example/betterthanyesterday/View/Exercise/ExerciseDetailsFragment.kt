package com.example.betterthanyesterday.View.Exercise

import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterthanyesterday.ExerciseViewModel
import com.example.betterthanyesterday.ExerciseViewModelFactory
import com.example.betterthanyesterday.Repository.ExerciseRepository
import com.example.betterthanyesterday.View.Adapter.ExercisesAdapter
import com.example.betterthanyesterday.databinding.FragmentExerciseDetailsBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.utils.ColorTemplate
import org.koin.android.ext.android.get

class ExerciseDetailsFragment : Fragment() {

    private var binding: FragmentExerciseDetailsBinding? = null
    private var selectedDate: String = ""

    // ViewModel 초기화
    private val viewModel: ExerciseViewModel by viewModels {
        ExerciseViewModelFactory(ExerciseRepository(weatherAPI = get()))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExerciseDetailsBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedDate = arguments?.getString("selectedDate") ?: ""
        binding?.dateTxt?.text = "날짜: $selectedDate"

        val adapter = ExercisesAdapter(emptyList())
        binding?.recExercises?.layoutManager = LinearLayoutManager(requireContext())
        binding?.recExercises?.adapter = adapter

        viewModel.exerciseRecords.observe(viewLifecycleOwner) { records ->
            adapter.updateRecords(records)
            calculateTotalCalories(records)
            setupBarChart(records)
        }

        viewModel.loadExerciseRecords(selectedDate)

        binding?.addExerciseBtn?.setOnClickListener {
            val exerciseType = binding?.exerciseTypeText?.text.toString()
            val calories = binding?.caloriesText?.text.toString().toIntOrNull() ?: 0

            if (exerciseType.isNotBlank() && calories > 0) {
                val record = ExerciseRecord(type = exerciseType, caloriesBurned = calories, date = selectedDate) // 'date' 추가
                viewModel.addExerciseRecord(selectedDate, record)
            } else {
                Toast.makeText(requireContext(), "운동 종류와 소모 칼로리를 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun calculateTotalCalories(records: List<ExerciseRecord>) {
        val totalCalories = records.sumOf { it.caloriesBurned }
        binding?.totalCaloriesTxt?.text = "총 소모 칼로리: $totalCalories kcal"
    }

    private fun setupBarChart(records: List<ExerciseRecord>) {
        val barChart = binding?.barChart

        val caloriesByType = records.groupBy { it.type }
            .mapValues { entry -> entry.value.sumOf { it.caloriesBurned } }

        val entries = caloriesByType.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }

        val dataSet = BarDataSet(entries, "운동별 칼로리 소모량")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        barChart?.apply {
            data = barData
            description.text = "운동 종류별 칼로리 비교"
            setFitBars(true)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}