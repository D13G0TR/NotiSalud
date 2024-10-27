package com.example.notisalud_main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UsuariosViewModel(private val repository: UsuariosRepository) : ViewModel() {

    private val _autenticadoUsuario = MutableLiveData<Usuarios?>()
    val autenticadoUsuario: LiveData<Usuarios?> get() = _autenticadoUsuario

    private val _usuariosRecientes = MutableLiveData<List<Usuarios>>()
    val usuariosRecientes: LiveData<List<Usuarios>> get() = _usuariosRecientes

    private val _registroExitoso = MutableLiveData<Boolean>()
    val registroExitoso: LiveData<Boolean> get() = _registroExitoso

    // Función para registrar un usuario
    fun registrarUsuario(run: String, nombre: String, contrasena: String, tipo: String) {
        val usuario = Usuarios(run = run, nombre = nombre, contrasena = contrasena, tipo = tipo)
        viewModelScope.launch {
            repository.registrarUsuario(usuario)
            _registroExitoso.value = true  // Indica que el registro fue exitoso
            _registroExitoso.value = false // Reinicia el valor para futuros registros
        }
    }

    // Función para autenticar usuario
    fun autenticarUsuario(run: String, contrasena: String) {
        viewModelScope.launch {
            _autenticadoUsuario.value = repository.autenticarUsuario(run, contrasena)
        }
    }

    // Función para obtener los últimos usuarios registrados
    fun obtenerUltimosUsuarios() {
        viewModelScope.launch {
            _usuariosRecientes.value = repository.obtenerUltimosUsuarios()
        }
    }
}