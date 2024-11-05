package com.example.betterthanyesterday

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.betterthanyesterday.R
//import com.example.healthapp.databinding.ActivityMainBinding
import com.example.betterthanyesterday.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 초기 화면 설정
        replaceFragment(WorkoutFragment())

        binding.btnWorkoutLog.setOnClickListener {
            replaceFragment(WorkoutFragment())
        }

        binding.btnChart.setOnClickListener {
            replaceFragment(ChartsFragment())
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}