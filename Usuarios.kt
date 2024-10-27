package com.example.notisalud_main

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuarios(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,  // Generación automática de ID
    val run: String,             // Identificador del usuario (RUT o RUN)
    val nombre: String,          // Nombre del usuario
    val contrasena: String,       // Contraseña del usuario
    val tipo: String              // Tipo de usuario (e.g., paciente, enfermera, etc.)
)