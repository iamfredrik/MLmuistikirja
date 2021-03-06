package com.example.mlmuistikirja

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MuistikirjaViewModel(private val repository: MuistikirjaRepository) : ViewModel() {

    val muistikirjat: LiveData<List<Muistikirja>> = repository.getMuistikirjat

    fun insert(muistikirja: Muistikirja) = viewModelScope.launch {
        repository.insert(muistikirja)
    }

    class MuistikirjaViewModelFactory(private val repository: MuistikirjaRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MuistikirjaViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MuistikirjaViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}