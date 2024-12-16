package com.example.betterthanyesterday

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.betterthanyesterday.databinding.ListBudgetsBinding
import com.example.betterthanyesterday.databinding.ListTodosBinding

data class BudgetRecord(
    val choice: String = "",       // 수입/지출 구분
    val category: String = "",     // 카테고리 (예: 음식, 교통 등)
    val mount: Int = 0,            // 금액
    val year: Int = 0,             // 연도
    val month: Int = 0,            // 월
    val day: Int = 0,              // 일
)

class BudgetsAdapter(
    private var budgets : List<BudgetRecord>,
    private val onDeleteClick: (BudgetRecord) -> Unit // 삭제 콜백
) : RecyclerView.Adapter<BudgetsAdapter.BudgetViewHolder>() {

    inner class BudgetViewHolder(val binding: ListBudgetsBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BudgetViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListBudgetsBinding.inflate(inflater, parent, false)
        return BudgetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BudgetViewHolder, position: Int) {
        val record = budgets[position]  //position: 현재 항목 인덱스
        holder.binding.choiceTxt.text = record.choice
        holder.binding.contentTxt.text = record.category
        holder.binding.mountTxt.text = record.mount.toString()

        // 삭제 버튼 클릭 이벤트 설정
        holder.binding.deleteBtn.setOnClickListener {
            onDeleteClick(record) // 콜백 호출
        }
    }

    //리사이클러뷰에 표시할 항목 수
    override fun getItemCount() = budgets.size

    //가계부 갱신
    fun updateRecords(newRecords: List<BudgetRecord>) {
        budgets = newRecords
        notifyDataSetChanged()
    }
}