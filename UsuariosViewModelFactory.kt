package com.example.notisalud_main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class UsuariosViewModelFactory(private val repository: UsuariosRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UsuariosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UsuariosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}