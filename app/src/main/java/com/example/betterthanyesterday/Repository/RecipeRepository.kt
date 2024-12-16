package com.example.betterthanyesterday.Repository

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.betterthanyesterday.Viewmodel.Recipe
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class RecipeRepository {

    val database = Firebase.database
    val recipeRef = database.getReference("RecipeList")
    private val firestore = FirebaseFirestore.getInstance()

    // Recipe observing in real-time
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

    // recipe upload (sorting : recipe 1 ~ recipe (num))
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

    //recipe delete - title : id
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
    // update recipe : title can't update
    fun updateRecipeInDatabase(updatedRecipe: Recipe) {
        recipeRef.orderByChild("title").equalTo(updatedRecipe.title)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children) {
                        child.ref.setValue(updatedRecipe)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("RecipeRepository", "Failed to update recipe: ${error.message}")
                }
            })
    }

    fun uploadImageToFirebase(uri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("recipe_images/${System.currentTimeMillis()}.jpg")
        storageRef.putFile(uri)
            .addOnSuccessListener { taskSnapshot ->
                storageRef.downloadUrl.addOnSuccessListener { url ->
                    onSuccess(url.toString())
                }
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}


