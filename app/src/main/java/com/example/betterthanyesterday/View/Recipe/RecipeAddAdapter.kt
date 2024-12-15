package com.example.betterthanyesterday.View.Recipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.betterthanyesterday.R

data class Ingredient(
    val name: String,
    val quantity: String
)
class IngredientsAdapter(private val ingredients: MutableList<Ingredient>)
    : RecyclerView.Adapter<IngredientsAdapter.IngredientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = LayoutInflater.from(parent.context).inflate(R.layout.list_ingredient, parent, false)
        return IngredientViewHolder(binding)
    }
    class IngredientViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameEditText: EditText = view.findViewById(R.id.edt_ingredient_name)
        val quantityEditText: EditText = view.findViewById(R.id.edt_ingredient_qty)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val ingredient = ingredients[position]
        holder.nameEditText.setText(ingredient.name)
        holder.quantityEditText.setText(ingredient.quantity)
    }

    override fun getItemCount() = ingredients.size

    fun addIngredient() {
        ingredients.add(Ingredient("", ""))
        notifyItemInserted(ingredients.size - 1)
    }
    fun getUpdatedIngredients(): List<Ingredient> {
        return ingredients
    }
}