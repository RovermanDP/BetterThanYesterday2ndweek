package com.example.betterthanyesterday

import WorkoutViewModel
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterthanyesterday.databinding.FragmentChartsBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry


class ChartsFragment : Fragment() {

    private lateinit var binding: FragmentChartsBinding
    private val workoutViewModel: WorkoutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartsBinding.inflate(inflater, container, false)

        setupRecyclerView()
        setupBarChart()

        workoutViewModel.exerciseRecords.observe(viewLifecycleOwner) { records ->
            updateRecyclerView(records)
            updateBarChart(records)
        }

        workoutViewModel.loadExerciseRecords() // 데이터 로드

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = WorkoutAdapter(emptyList())
        }
    }

    private fun updateRecyclerView(records: List<ExerciseRecord>) {
        (binding.recyclerView.adapter as WorkoutAdapter).updateRecords(records)
    }

    private fun setupBarChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            axisLeft.axisMinimum = 0f
        }
    }

    private fun updateBarChart(records: List<ExerciseRecord>) {
        val entries = records.mapIndexed { index, record ->
            BarEntry(index.toFloat(), record.duration.toFloat())
        }

        val dataSet = BarDataSet(entries, "Duration (minutes)").apply {
            color = Color.BLACK
        }
        val data = BarData(dataSet)

        binding.barChart.apply {
            this.data = data
            notifyDataSetChanged()
            invalidate() // 차트를 다시 그립니다.
        }
    }
}




