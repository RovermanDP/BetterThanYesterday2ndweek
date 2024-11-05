package com.example.betterthanyesterday

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.betterthanyesterday.databinding.FragmentWorkoutBinding

class WorkoutFragment : Fragment() {

    private lateinit var binding: FragmentWorkoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWorkoutBinding.inflate(inflater, container, false)


        return binding.root
    }
}

