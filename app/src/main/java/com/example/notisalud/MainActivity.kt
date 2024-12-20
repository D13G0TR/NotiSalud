package com.example.notisalud

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notisalud.Enfermero.EnfermeroVista
import com.example.notisalud.Examen.LaboratorioVista
import com.example.notisalud.Medico.MedicoVista
import com.example.notisalud.Paciente.PacienteVista
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.notisalud.Paramedico.ParamedicoVista


class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        requestNotificationPermission()

        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    LoginScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth(),
                        onLogin = { email, password ->
                            signInUser(email, password)
                        },
                        onRegister = {
                            val intent = Intent(this, RegistroActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                    if (!isGranted) {
                        Toast.makeText(
                            this,
                            "Permiso de notificaciones denegado. Las notificaciones no funcionarán.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }

            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Verifica el rol del usuario en Firestore
                        firestore.collection("Users").document(userId).get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val rol = document.getString("rol")
                                    val firstName = document.getString("nombre") ?: "Usuario"
                                    val lastName = document.getString("apellido") ?: "Desconocido"

                                    when (rol) {
                                        "Paciente" -> {
                                            val intent = Intent(this, PacienteVista::class.java).apply {
                                                putExtra("firstName", firstName)
                                                putExtra("lastName", lastName)
                                            }
                                            startActivity(intent)
                                            finish()
                                        }
                                        "Enfermero" -> {
                                            val intent = Intent(this, EnfermeroVista::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        "Doctor" -> {
                                            val intent = Intent(this, MedicoVista::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        "Paramedico" -> {
                                            val intent = Intent(this, ParamedicoVista::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        "Laboratorio" -> {
                                            val intent = Intent(this, LaboratorioVista::class.java)
                                            startActivity(intent)
                                            finish()
                                        }
                                        else -> {
                                            Toast.makeText(this, "Rol no reconocido.", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(this, "Datos de usuario no encontrados.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Error al obtener datos del usuario.", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(
                        baseContext,
                        "Error de autenticación: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    @Composable
    fun LoginScreen(
        modifier: Modifier = Modifier,
        onLogin: (String, String) -> Unit,
        onRegister: () -> Unit
    ) {
        Box(modifier = modifier.fillMaxSize()) {
        // Fondo pantalla
        val background = painterResource(id = R.drawable.fondo)
        Image(
            painter = background,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Slogan superior
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "Bienvenido a NotiSalud",
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Tu salud nos importa",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var email by remember { mutableStateOf("") }
                var password by remember { mutableStateOf("") }

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                Button(
                    onClick = { onLogin(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Text("Iniciar sesión")
                }

                Button(
                    onClick = onRegister,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrarse")
                }
            }

            // Logo inferior
            val logo = painterResource(id = R.drawable.logo)
            Image(
                painter = logo,
                contentDescription = "Logo NotiSalud",
                modifier = Modifier
                    .size(120.dp)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    AppTheme {
        LoginScreen(
            onLogin = { _, _ -> },
            onRegister = {}
        )
    }
    }
}