package com.example.betterthanyesterday

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.betterthanyesterday.databinding.ItemWorkoutBinding
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

data class ExerciseRecord(
    val name: String = "",
    val date: Timestamp = Timestamp.now(),
    val duration: Int = 0,
    val calories: Int = 0
)

class WorkoutAdapter(private var recordList: List<ExerciseRecord>) :
    RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    inner class WorkoutViewHolder(val binding: ItemWorkoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(record: ExerciseRecord) {
            binding.txtWorkoutName.text = record.name
            binding.txtWorkoutDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(record.date.toDate())
            binding.txtDuration.text = "${record.duration} Mins"
            binding.txtCalories.text = "${record.calories} Kcal"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        holder.bind(recordList[position])
    }

    override fun getItemCount() = recordList.size

    fun updateRecords(newRecords: List<ExerciseRecord>) {
        recordList = newRecords
        notifyDataSetChanged()
    }
}
