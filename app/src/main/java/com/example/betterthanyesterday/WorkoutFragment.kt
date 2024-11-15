package com.example.betterthanyesterday

import WorkoutViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.betterthanyesterday.databinding.FragmentWorkoutBinding
import com.google.firebase.Timestamp

class WorkoutFragment : Fragment() {

    private lateinit var binding: FragmentWorkoutBinding
    private val workoutViewModel: WorkoutViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkoutBinding.inflate(inflater, container, false)

        binding.btnAddWorkout.setOnClickListener {
            val name = binding.editWorkoutName.text.toString()
            val duration = binding.editDuration.text.toString().toIntOrNull() ?: 0
            val calories = binding.editCalories.text.toString().toIntOrNull() ?: 0
            val date = Timestamp.now()

            // 모든 값을 포함한 ExerciseRecord 객체 생성
            val record = ExerciseRecord(name = name, date = date, duration = duration, calories = calories)

            // viewModel에 추가
            workoutViewModel.addExerciseRecord(record)
        }

        return binding.root
    }
}
