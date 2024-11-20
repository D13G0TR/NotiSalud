package com.example.notisalud.Laboratorio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme

// Datos del paciente para la vista de laboratorio
data class PacienteLaboratorio(val nombres: String, val estadoExamen: String)

val pacientesLaboratorio = listOf(
    PacienteLaboratorio("Juan Pérez", "Pendiente"),
    PacienteLaboratorio("Ana López", "Pendiente"),
    PacienteLaboratorio("Carlos Ramírez", "Pendiente")
)

class LaboratorioVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                LaboratorioVistaScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun LaboratorioVistaScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Lista de Pacientes para Exámenes:", modifier = Modifier.padding(bottom = 8.dp))

        // Mostrar lista de pacientes
        pacientesLaboratorio.forEach { paciente ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Columna de nombre
                Text(text = paciente.nombres, modifier = Modifier.weight(1f))

                // Columna de estado del examen
                Text(text = paciente.estadoExamen, modifier = Modifier.weight(1f))

                // Botones para acciones
                Row(
                    modifier = Modifier.weight(1.2f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Botón para marcar examen como listo
                    Button(onClick = { onExamenListoClick(paciente) }) {
                        Text("Examen listo")
                    }
                }
            }
        }
    }
}

// Función para recibir examen (simulada)
fun onRecepcionarClick(paciente: PacienteLaboratorio) {
    // Aquí podríam el estado del paciente o realizar alguna acción
    println("Examen de ${paciente.nombres} recepcionado.")
}

// Función para marcar examen como listo (simulada)
fun onExamenListoClick(paciente: PacienteLaboratorio) {
    // Aquí podríamos actualizar el estado del examen o realizar alguna acción
    println("Examen de ${paciente.nombres} marcado como listo.")
}

@Preview(showSystemUi = true)
@Composable
fun LaboratorioVistaPreview() {
    AppTheme {
        LaboratorioVistaScreen(modifier = Modifier.fillMaxSize())
    }
}