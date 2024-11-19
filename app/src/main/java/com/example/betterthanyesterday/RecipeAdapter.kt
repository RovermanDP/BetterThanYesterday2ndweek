package com.example.betterthanyesterday

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.betterthanyesterday.viewmodel.Recipe

class RecipeAdapter(
    private var recipes: MutableList<Recipe>,
    private val onDelete: (Recipe) -> Unit) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recipeTitle: TextView = view.findViewById(R.id.recipe_title)
        val recipeImage: ImageView = view.findViewById(R.id.img_recipe)
        val deleteButton: Button = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]

        holder.recipeTitle.text = recipe.title
        holder.recipeImage.setImageResource(R.drawable.food)
        holder.deleteButton.setOnClickListener {
            onDelete(recipe)
        }
    }

    override fun getItemCount(): Int = recipes.size

    fun updateRecipes(newRecipes: List<Recipe>) {
        recipes.clear()
        recipes.addAll(newRecipes)
        notifyDataSetChanged()
    }
}