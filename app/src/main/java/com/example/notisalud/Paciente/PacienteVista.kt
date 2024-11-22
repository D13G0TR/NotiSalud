package com.example.notisalud.Paciente

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.MainActivity
import com.example.notisalud.Paciente.PacienteActivity
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.platform.LocalContext

class PacienteVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Recibe el nombre y apellido del usuario
            val firstName = intent.getStringExtra("firstName") ?: "Nombre no disponible"
            val lastName = intent.getStringExtra("lastName") ?: "Apellido no disponible"

            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    PacienteVistaScreen(
                        firstName = firstName,
                        lastName = lastName,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth(),
                        onUrgenciasClick = {
                            // Redirige a PacienteActivity
                            val intent = Intent(this, PacienteActivity::class.java).apply {
                                putExtra("firstName", firstName)
                                putExtra("lastName", lastName)
                            }
                            startActivity(intent)
                        },
                        onLogout = { logoutUser() }
                    )
                }
            }
        }
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut() // Cierra la sesión del usuario
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()

        // Redirige a MainActivity (pantalla de login)
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

@Composable
fun PacienteVistaScreen(
    firstName: String,
    lastName: String,
    modifier: Modifier = Modifier,
    onUrgenciasClick: () -> Unit = {},
    onLogout: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido, $firstName $lastName",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = onUrgenciasClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Ingreso a Urgencias")
        }

        Button(
            onClick = {
                // Aquí debe ir la lógica para abrir la ventana de notificaciones
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Notificaciones")
        }

        Button(
            onClick = {
                // Aquí debe ir la lógica para abrir el historial médico
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Historial Médico")
        }

        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PacienteVistaScreenPreview() {
    AppTheme {
        PacienteVistaScreen(
            firstName = "Juan",
            lastName = "Pérez",
            onLogout = {}
        )
    }
}