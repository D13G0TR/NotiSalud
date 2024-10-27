package com.example.notisalud_main

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [Usuarios::class], version = 1, exportSchema = false)
abstract class UsuariosDatabase : RoomDatabase() {

    abstract fun usuariosDao(): UsuariosDao

    companion object {
        @Volatile
        private var INSTANCE: UsuariosDatabase? = null

        fun getDatabase(context: Context): UsuariosDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UsuariosDatabase::class.java,
                    "usuarios_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}