package com.example.betterthanyesterday.Repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.betterthanyesterday.ExerciseRecord
import com.example.betterthanyesterday.viewmodel.Recipe
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class RecipeRepository {

    val database = Firebase.database
    val recipeRef = database.getReference("RecipeList")
    private val firestore = FirebaseFirestore.getInstance()

    fun observeRecipe(recipe: MutableLiveData<MutableList<Recipe>>) {
        recipeRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recipes = mutableListOf<Recipe>()
                for (recipeSnapshot in snapshot.children) {
                    val recipeItem = recipeSnapshot.getValue(Recipe::class.java)
                    recipeItem?.let { recipes.add(it) }
                }
                recipe.postValue(recipes)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RecipeRepository", "Database error: ${error.message}")
            }
        })
    }

    fun postRecipe(recipe: Recipe) {
        recipeRef.orderByKey().limitToLast(1).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var newKey = "recipe1"
                if (snapshot.exists()) {
                    val lastKey = snapshot.children.last().key ?: "recipe0"
                    val lastNumber = lastKey.removePrefix("recipe").toIntOrNull() ?: 0
                    newKey = "recipe${lastNumber + 1}"
                }
                recipeRef.child(newKey).setValue(recipe)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("RecipeRepository", "Failed to fetch last key: ${error.message}")
            }
        })
    }

    fun deleteRecipeFromDatabase(recipe: Recipe) {
        recipeRef.orderByChild("title").equalTo(recipe.title)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        child.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("RecipeRepository", "Failed to delete recipe: ${error.message}")
                }
            })
    }

    suspend fun addRecipeRecords(record: Recipe) {
        firestore.collection("recipeRecords").add(record).await()
    }

    suspend fun getRecipeRecords(): List<Recipe> {
        val snapshot = firestore.collection("recipeRecords").get().await()
        return snapshot.documents.mapNotNull { it.toObject(Recipe::class.java) }
    }
}


