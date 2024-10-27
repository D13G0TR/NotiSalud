package com.example.notisalud_main

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UsuariosDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertUsuario(usuario: Usuarios)

    @Query("SELECT * FROM usuarios WHERE run = :run AND contrasena = :contrasena LIMIT 1")
    suspend fun autenticarUsuario(run: String, contrasena: String): Usuarios?

    @Query("SELECT * FROM usuarios ORDER BY id DESC LIMIT 5")
    suspend fun getUltimosUsuarios(): List<Usuarios>
}