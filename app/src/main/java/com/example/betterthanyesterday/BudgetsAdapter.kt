package com.example.betterthanyesterday

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.betterthanyesterday.databinding.ListBudgetsBinding
import com.example.betterthanyesterday.databinding.ListTodosBinding

data class BudgetRecord(
    val choice : String = "",
    val category : String = "",
    val mount : Int = 0
)

class BudgetsAdapter(private var budgets : List<BudgetRecord>)
    : RecyclerView.Adapter<BudgetsAdapter.BudgetViewHolder>() {

        inner class BudgetViewHolder(val binding: ListBudgetsBinding) :
            RecyclerView.ViewHolder(binding.root) {
                fun bind(record: BudgetRecord) {
                    binding.contentTxt.text = record.category
                    binding.mountTxt.text = "${record.mount}"
                    binding.choiceTxt.text = record.choice
                    
                }
            }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val binding = ListBudgetsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BudgetViewHolder(binding)
    }

    override fun getItemCount() = budgets.size

    override fun onBindViewHolder(holder: BudgetViewHolder, position : Int ) {
        holder.bind(budgets[position])
    }

    fun updateRecords(newRecords:  List<BudgetRecord>) {
        budgets = newRecords
        notifyDataSetChanged()
    }



}

