package com.example.betterthanyesterday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.betterthanyesterday.databinding.ItemWorkoutBinding

data class Workout(val name: String, val date: String, val duration: Int, val calories: Int)

class WorkoutAdapter(private val workoutList: List<Workout>) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    inner class WorkoutViewHolder(val binding: ItemWorkoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(workout: Workout) {
            binding.txtWorkoutName.text = workout.name
            binding.txtWorkoutDate.text = workout.date
            binding.txtDuration.text = "${workout.duration} Mins"
            binding.txtCalories.text = "${workout.calories} Kcal"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(workoutList[position])
    }

    override fun getItemCount() = workoutList.size
}