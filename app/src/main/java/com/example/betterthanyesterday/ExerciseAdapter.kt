package com.example.betterthanyesterday

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import data.Model.ExerciseRecord


class ExercisesAdapter(private var records: List<ExerciseRecord>) :
    RecyclerView.Adapter<ExercisesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val workoutName: TextView = view.findViewById(R.id.txtWorkoutName)
        val caloriesBurned: TextView = view.findViewById(R.id.txtCalories)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_exercise_record, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = records[position]
        holder.workoutName.text = record.type
        holder.caloriesBurned.text = "${record.caloriesBurned} kcal"
    }

    override fun getItemCount() = records.size

    fun updateRecords(newRecords: List<ExerciseRecord>) {
        records = newRecords
        notifyDataSetChanged()
    }
}


