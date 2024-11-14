package com.example.notisalud

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notisalud.Paciente.PacienteVista
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
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

    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Iniciar PacienteActivity si el inicio de sesión es exitoso
                        val intent = Intent(this, PacienteVista::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        // Mostrar mensaje de error si el userId es nulo
                        Toast.makeText(this, "Error de autenticación: Usuario no encontrado.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Mostrar mensaje de error si la autenticación falla
                    Toast.makeText(baseContext, "Error de autenticación: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
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
            // Título y eslogan
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                ) {
                    Text(
                        text = "Bienvenido a NotiSalud",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Surface(
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text(
                        text = "Tu salud nos importa",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Campos de email y contraseña con botones
            Column(
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
                        .padding(top = 16.dp)
                )

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )

                Button(
                    onClick = { onLogin(email, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Text("Iniciar sesión")
                }

                Button(
                    onClick = onRegister,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("Registrarse")
                }
            }

            LogoWrapper(modifier = Modifier.padding(bottom = 32.dp))
        }
    }
}

@Composable
fun LogoWrapper(modifier: Modifier = Modifier) {
    val image = painterResource(id = R.drawable.logo)
    Image(
        painter = image,
        contentDescription = "Logo NotiSalud",
        modifier = modifier
    )
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
