package com.example.betterthanyesterday.View.Recipe

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.betterthanyesterday.R
import com.example.betterthanyesterday.databinding.DialogEditRecipeBinding
import com.example.betterthanyesterday.Viewmodel.Recipe
import com.example.betterthanyesterday.Viewmodel.RecipeViewModel

class EditRecipeDialogFragment(
    private val recipe: Recipe,
    private val onUpdate: (Recipe) -> Unit
) : DialogFragment() {

    private var _binding: DialogEditRecipeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RecipeViewModel by activityViewModels()

    private var imageUri: Uri? = null
    private lateinit var imageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                imageUri = result.data?.data
                if (imageUri != null) {
                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .into(binding.recipeImage)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogEditRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etTitle.setText(recipe.title)
        binding.etTime.setText(recipe.time)
        binding.etCalories.setText(recipe.calories.toString())
        binding.etIngredients.setText(
            recipe.ingredients.split(",").joinToString("\n") { it.trim() }
        )
        binding.etDescription.setText(recipe.description)

        if (recipe.imageUrl.isNotBlank()) {
            Glide.with(this)
                .load(recipe.imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(binding.recipeImage)
        }

        binding.btnFoodImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            imageLauncher.launch(intent)
        }

        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString()
            val time = binding.etTime.text.toString()
            val calories = binding.etCalories.text.toString().toIntOrNull() ?: 0
            val ingredients = binding.etIngredients.text.toString()
            val description = binding.etDescription.text.toString()

            imageUri?.let { uri ->
                viewModel.uploadImageAndSaveRecipe(
                    recipe.copy(
                        title = title,
                        time = time,
                        calories = calories,
                        ingredients = ingredients,
                        description = description
                    ),
                    uri
                )
            } ?: run {
                viewModel.updateRecipe(
                    recipe.copy(
                        title = title,
                        time = time,
                        calories = calories,
                        ingredients = ingredients,
                        description = description
                    )
                )
            }
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog ?: return
        val width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.7).toInt()
        dialog.window?.setLayout(width, height)
    }
}