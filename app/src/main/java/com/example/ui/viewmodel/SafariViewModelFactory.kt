package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.audio.SafariAudioEngine
import com.example.data.local.AppDatabase
import com.example.data.repository.SafariRepository

class SafariViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SafariViewModel::class.java)) {
            val database = AppDatabase.getDatabase(context)
            val repository = SafariRepository(database)
            val audioEngine = SafariAudioEngine(context)
            return SafariViewModel(repository, audioEngine) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
