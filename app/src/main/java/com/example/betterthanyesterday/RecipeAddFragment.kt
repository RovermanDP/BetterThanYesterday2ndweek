package com.example.betterthanyesterday

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterthanyesterday.databinding.FragmentRecipeAddBinding
import com.example.betterthanyesterday.viewmodel.Recipe
import com.example.betterthanyesterday.viewmodel.RecipeViewModel

class RecipeAddFragment : Fragment() {

    private val viewModel: RecipeViewModel by activityViewModels()

    private val ingredientsAdapter by lazy { IngredientsAdapter(mutableListOf()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRecipeAddBinding.inflate(inflater, container, false)

        binding.ingredientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ingredientsAdapter
        }

        binding.btnAddMoreIngredients.setOnClickListener {
            ingredientsAdapter.addIngredient()
        }

        binding.btnSaveIngredients.setOnClickListener {
            binding.ingredientsRecyclerView.clearFocus()
        }

        binding.btnSaveRecipe.setOnClickListener {
            val title = binding.edtTitleRecipe.text.toString()
            val time = binding.edtTimeRecipe.text.toString()
            val calories = binding.edtCaloriesRecipe.text.toString().toIntOrNull() ?: 0
            val description = binding.edtDescriptionRecipe.text.toString()

            if (title.isNotBlank()) {
                val updatedIngredients = ingredientsAdapter.getUpdatedIngredients()
                val newRecipe = Recipe(
                    title = title,
                    time = time,
                    calories = calories,
                    ingredients = updatedIngredients.joinToString { "${it.name} (${it.quantity})" },
                    description = description
                )
                viewModel.addRecipe(newRecipe)
                findNavController().navigate(R.id.action_RecipeAddFragment_to_recipeFragment)
            }
        }

        return binding.root
    }
}
