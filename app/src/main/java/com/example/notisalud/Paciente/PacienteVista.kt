package com.example.notisalud.Paciente

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notisalud.MainActivity
import com.example.notisalud.ui.theme.AppTheme

class PacienteVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recupera el nombre y apellido del intent
        val firstName = intent.getStringExtra("firstName") ?: "Nombre no disponible"
        val lastName = intent.getStringExtra("lastName") ?: "Apellido no disponible"

        setContent {
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
                        onCloseSessionClick = {
                            // Redirige a MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish() // Finaliza la actividad actual para evitar volver con el botón "Atrás"
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PacienteVistaScreen(
    firstName: String,
    lastName: String,
    modifier: Modifier = Modifier,
    onUrgenciasClick: () -> Unit,
    onCloseSessionClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Muestra el nombre y apellido del usuario
        Text(
            text = "Bienvenido $firstName $lastName",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Botón para ingresar a urgencias
        Button(
            onClick = onUrgenciasClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Ingreso a Urgencias")
        }

        // Botón de otras funcionalidades (ejemplo)
        Button(
            onClick = { /* Implementar más acciones si es necesario */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Historial de Notificaciónes")
        }

        // Botón de cerrar sesión
        Button(
            onClick = onCloseSessionClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
fun PacienteVistaPreview() {
    AppTheme {
        PacienteVistaScreen(
            firstName = "Juan",
            lastName = "Pérez",
            onUrgenciasClick = {},
            onCloseSessionClick = {}
        )
    }
}