package com.example.betterthanyesterday.View.Recipe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.betterthanyesterday.R
import com.example.betterthanyesterday.Viewmodel.Recipe
import com.example.betterthanyesterday.Viewmodel.RecipeViewModel
import com.example.betterthanyesterday.databinding.FragmentRecipeAddBinding

class RecipeAddFragment : Fragment() {

    private val viewModel: RecipeViewModel by activityViewModels()
    private val ingredientsAdapter by lazy { IngredientsAdapter(mutableListOf()) }

    private lateinit var binding: FragmentRecipeAddBinding
    private var imageUri: Uri? = null
    private lateinit var imageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                imageUri = result.data?.data
                binding.recipeImage.apply {
                    setImageURI(imageUri)
                    scaleType = ImageView.ScaleType.CENTER_CROP
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRecipeAddBinding.inflate(inflater, container, false)

        binding.ingredientsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = ingredientsAdapter
        }

        binding.btnAddMoreIngredients.setOnClickListener {
            ingredientsAdapter.addIngredient()
        }

        binding.btnFoodImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imageLauncher.launch(intent)
        }

        binding.btnSaveRecipe.setOnClickListener {
            val title = binding.edtTitleRecipe.text.toString()
            val time = binding.edtTimeRecipe.text.toString()
            val calories = binding.edtCaloriesRecipe.text.toString().toIntOrNull() ?: 0
            val description = binding.edtDescriptionRecipe.text.toString()

            val finalImageUri = imageUri ?: Uri.parse("android.resource://${requireContext().packageName}/${R.drawable.ic_launcher_foreground}")

            if (title.isNotBlank()) {
                val updatedIngredients = ingredientsAdapter.getUpdatedIngredients()
                val newRecipe = Recipe(
                    title = title,
                    time = time,
                    calories = calories,
                    ingredients = updatedIngredients.joinToString { "${it.name} (${it.quantity})" },
                    description = description
                )

                viewModel.addRecipeWithImage(newRecipe, finalImageUri)

                findNavController().navigate(R.id.action_RecipeAddFragment_to_recipeFragment)
            }
        }

        return binding.root
    }
}
