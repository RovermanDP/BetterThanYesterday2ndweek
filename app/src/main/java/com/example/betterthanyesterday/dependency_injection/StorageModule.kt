package com.example.betterthanyesterday.dependency_injection

import com.example.betterthanyesterday.storage.SharedPreferencesManager
import org.koin.dsl.module

val storageModule = module {
    single{ SharedPreferencesManager(context=get(), gson = get()) }
}