package com.example.mlmuistikirja

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MuistikirjaViewModel(private val repository: MuistikirjaRepository) : ViewModel() {

    val muistikirjat: LiveData<List<Muistikirja>> = repository.getMuistikirjat.asLiveData()

    fun insert(muistikirja: Muistikirja) = viewModelScope.launch {
        repository.insert(muistikirja)
    }

    fun update(muistikirja: Muistikirja) = viewModelScope.launch {
        repository.update(muistikirja)
    }
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