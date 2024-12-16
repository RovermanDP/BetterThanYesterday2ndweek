package com.example.betterthanyesterday.dependency_injection

import com.example.betterthanyesterday.ExerciseViewModel
import com.example.betterthanyesterday.Repository.ExerciseRepository
import com.example.betterthanyesterday.Viewmodel.LocationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { ExerciseViewModel(repository = get()) }
    viewModel { LocationViewModel(exerciseRepository = get()) }
}
