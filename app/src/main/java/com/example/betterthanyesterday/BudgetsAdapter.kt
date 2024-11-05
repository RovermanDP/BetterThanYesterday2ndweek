package com.example.betterthanyesterday

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.betterthanyesterday.TodoAdapter.Holder
import com.example.betterthanyesterday.databinding.ListBudgetsBinding
import com.example.betterthanyesterday.databinding.ListTodosBinding

class BudgetsAdapter(val budgets : Array<Budget>)
    : RecyclerView.Adapter<BudgetsAdapter.Holder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ListBudgetsBinding.inflate(LayoutInflater.from(parent.context))
        return Holder(binding)
    }

    override fun getItemCount() = budgets.size

    override fun onBindViewHolder(holder: Holder, position : Int ) {
        holder.bind(budgets[position])
    }

    class Holder(private val binding: ListBudgetsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(budget : Budget){
            binding.gainlosssTxt.text = budget.gainloss
            binding.contentTxt.text = budget.content
            binding.mountTxt.text = budget.cost.toString()
            binding.imagedelete.setImageResource(R.drawable.budget_delete)
        }
    }
}

