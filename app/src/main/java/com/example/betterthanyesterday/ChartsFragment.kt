package com.example.betterthanyesterday
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterthanyesterday.databinding.FragmentChartsBinding

class ChartsFragment : Fragment() {

    private lateinit var binding: FragmentChartsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartsBinding.inflate(inflater, container, false)

        val workoutList = listOf(
            Workout("Push-up", "June 09 ", 25, 120),
            Workout("Running", "April 15 ", 30, 130)
        )
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = WorkoutAdapter(workoutList)
        }

        return binding.root
    }

}