package com.example.betterthanyesterday.Viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.betterthanyesterday.Repository.RecipeRepository


data class Recipe(
    val title: String = "",
    val time: String? = null,
    val calories: Int = 0,
    val ingredients: String = "",
    val description: String? = null
)
class RecipeViewModel : ViewModel() {

    private val recipeRepository = RecipeRepository()

    private val _recipes = MutableLiveData<MutableList<Recipe>>(mutableListOf())
    val recipes: LiveData<MutableList<Recipe>> get() = _recipes

    init {
        recipeRepository.observeRecipe(_recipes)
    }
    fun addRecipe(recipe: Recipe) {
        recipeRepository.postRecipe(recipe)
    }

    fun deleteRecipe(recipe: Recipe) {
        _recipes.value?.remove(recipe)
        _recipes.value = _recipes.value

        recipeRepository.deleteRecipeFromDatabase(recipe)

    }
    /*
    fun addRecipeRecord(record: Recipe) {
        viewModelScope.launch {
            recipeRepository.addRecipeRecords(record)
            loadRecipeRecords()
        }
    }

    fun addRecipe(recipe: Recipe) {
        recipeRepository.postRecipe(recipe)
    }
    fun deleteRecipe(recipe: Recipe) {
        _recipes.value?.remove(recipe)_recipes.value = _recipes.value
    }

    fun loadRecipeRecords() {
        viewModelScope.launch {
            _recipes.value = recipeRepository.getRecipeRecords()
        }
    }

    */
}