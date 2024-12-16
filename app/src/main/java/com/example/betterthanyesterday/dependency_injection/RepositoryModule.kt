package com.example.betterthanyesterday.dependency_injection

import com.example.betterthanyesterday.Repository.ExerciseRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { ExerciseRepository(weatherAPI = get()) }
}
