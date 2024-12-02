package com.example.betterthanyesterday

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.betterthanyesterday.databinding.FragmentExerciseDateBinding

class ExerciseDateFragment : Fragment() {

    private var binding: FragmentExerciseDateBinding? = null
    private var selectedDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentExerciseDateBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 캘린더에서 날짜 선택 이벤트
        binding?.calendarView?.setOnDateChangeListener { _, year, month, dayOfMonth ->
            selectedDate = "$year-${(month + 1).toString().padStart(2, '0')}-${dayOfMonth.toString().padStart(2, '0')}"
            binding?.selectedDateTxt?.text = "선택된 날짜: $selectedDate"
            binding?.goToDetailsBtn?.isEnabled = true
        }

        // 다음 화면으로 이동
        binding?.goToDetailsBtn?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("selectedDate", selectedDate)
            findNavController().navigate(R.id.action_exerciseDateFragment_to_exerciseDetailsFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
