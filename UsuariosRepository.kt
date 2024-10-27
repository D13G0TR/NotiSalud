package com.example.notisalud_main

class UsuariosRepository(private val usuariosDao: UsuariosDao) {

    // Función para registrar un nuevo usuario
    suspend fun registrarUsuario(usuario: Usuarios) {
        usuariosDao.insertUsuario(usuario)  // Inserta un usuario
    }

    // Función para autenticar a un usuario
    suspend fun autenticarUsuario(run: String, contrasena: String): Usuarios? {
        return usuariosDao.autenticarUsuario(run, contrasena) // Usa autenticarUsuario
    }

    // Función para obtener los últimos usuarios registrados (opcional)
    suspend fun obtenerUltimosUsuarios(): List<Usuarios> {
        return usuariosDao.getUltimosUsuarios()
    }
}