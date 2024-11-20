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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme

class PacienteVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    PacienteVistaScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth(),
                        onUrgenciasClick = { // Intent para redirigir a PacienteUrgencia
                            val intent = Intent(this, PacienteActivity::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PacienteVistaScreen(
    modifier: Modifier = Modifier,
    onUrgenciasClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Panel de Paciente",
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
            onClick = { /* aqui deberia esta la Lógica para abrir la ventana de notificaciones */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Notificaciones")
        }

        Button(
            onClick = { /* Aqui deberia estar la Lógica para abrir otras ventanas */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text("Historial Médico")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PacienteVistaScreenPreview() {
    AppTheme {
        PacienteVistaScreen()
    }
}