package com.example.betterthanyesterday

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.betterthanyesterday.databinding.FragmentHealthBinding

class HealthFragment : Fragment() {

    private lateinit var binding: FragmentHealthBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHealthBinding.inflate(inflater, container, false)

        // 초기 화면 설정
        replaceFragment(WorkoutFragment())

        // Workout Log 버튼 클릭 시 WorkoutFragment 로 전환
        binding.btnWorkoutLog.setOnClickListener {
            replaceFragment(WorkoutFragment())
        }

        // Chart 버튼 클릭 시 ChartsFragment 로 전환
        binding.btnChart.setOnClickListener {
            replaceFragment(ChartsFragment())
        }

        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
