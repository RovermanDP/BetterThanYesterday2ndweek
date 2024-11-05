package com.example.betterthanyesterday

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.betterthanyesterday.databinding.FragmentRecipeAddBinding
import com.example.betterthanyesterday.databinding.FragmentRecipeBinding


class RecipeAddFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentRecipeAddBinding.inflate(inflater,container,false)

        val recipe_title = arguments?.getString("recipe_title")
        val recipe_ingredient = arguments?.getString("recipe_ingredient")
        val recipe_explain = arguments?.getString("recipe_explain")

        binding.edtTitleRecipe.setText(recipe_title)
        binding.edtIngredient.setText(recipe_ingredient)
        binding.edtExplain.setText(recipe_explain)

        binding.btnAddRecipe.setOnClickListener {
            val newTitleRecipe = binding.edtTitleRecipe.text.toString()
            val newIngredient = binding.edtIngredient.text.toString()
            val newExplain = binding.edtExplain.text.toString()

            if (newTitleRecipe.isNotBlank()) {
                val bundle = Bundle().apply {
                    putString("newTitle", newTitleRecipe)
                    putString("newDetail", newIngredient)
                    putString("newExplain", newExplain)
                }
                findNavController().navigate(
                    R.id.action_RecipeAddFragment_to_recipeFragment,
                    bundle
                )
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }
}