package com.example.betterthanyesterday

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.betterthanyesterday.databinding.FragmentRecipeBinding
import com.example.betterthanyesterday.viewmodel.RecipeViewModel


class RecipeFragment : Fragment() {

    private val viewModel: RecipeViewModel by activityViewModels()

    private val recipeAdapter by lazy {
        RecipeAdapter(mutableListOf()) { recipe ->
            viewModel.deleteRecipe(recipe)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRecipeBinding.inflate(inflater, container, false)

        binding.listRecipes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recipeAdapter
        }

        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipeAdapter.updateRecipes(recipes ?: mutableListOf())
        }

        binding.btnAdd.setOnClickListener {
            findNavController().navigate(R.id.action_recipeFragment_to_RecipeAddFragment)
        }

        return binding.root
    }
}