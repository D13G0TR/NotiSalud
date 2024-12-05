package com.example.notisalud

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.Paciente.PacienteVista
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.platform.LocalContext

class RegistroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    RegistroScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth(),
                        onRegister = { nombre, apellido, edad, correo, password ->
                            registrarUsuario(nombre, apellido, edad, correo, password)
                        }
                    )
                }
            }
        }
    }

    private fun registrarUsuario(firstName: String, lastName: String, edad: String, email: String, password: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val userData = hashMapOf(
                        "nombre" to firstName,
                        "apellido" to lastName,
                        "edad" to edad,
                        "correo" to email,
                        "rol" to "Paciente"
                    )

                    FirebaseFirestore.getInstance()
                        .collection("Users")
                        .document(user!!.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(baseContext, "Registro exitoso", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(baseContext, "Error al guardar datos: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(baseContext, "Error al registrarse: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }




    @Composable
    fun RegistroScreen(
        modifier: Modifier = Modifier,
        onRegister: (String, String, String, String, String) -> Unit
    ) {
        var nombre by remember { mutableStateOf("") }
        var apellido by remember { mutableStateOf("") }
        var edad by remember { mutableStateOf("") }
        var correo by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        val context = LocalContext.current // Obtén el contexto aquí

        Column(
            modifier = modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            TextField(
                value = apellido,
                onValueChange = { apellido = it },
                label = { Text("Apellido") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            TextField(
                value = edad,
                onValueChange = { edad = it },
                label = { Text("Edad") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            TextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )

            Button(
                onClick = {
                    if (nombre.isNotEmpty() && apellido.isNotEmpty() && edad.isNotEmpty() && correo.isNotEmpty() && password.isNotEmpty()) {
                        onRegister(nombre, apellido, edad, correo, password)
                    } else {
                        // Usar el contexto aquí para el Toast
                        Toast.makeText(context, "Por favor complete todos los campos", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Registrar")
            }
        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun RegistroScreenPreview() {
        AppTheme {
            RegistroScreen { _, _, _, _, _ -> }
        }
    }
}