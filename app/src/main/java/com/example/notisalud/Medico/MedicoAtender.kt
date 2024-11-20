package com.example.notisalud.Medico

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme

// Datos del paciente
data class PacienteValidados(val nombres: String, val urgencias: String)

class MedicoAtender : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val nombrePaciente = intent.getStringExtra("nombre_paciente") // Obtiene el dato pasado
        setContent {
            AppTheme {
                MedicoAtenderScreen(pacienteNombre = nombrePaciente, onBack = { onBack() })
            }
        }
    }

    // Usa onBack() directamente en lugar de redefinir el método
    private fun onBack() {
        val intent = Intent(this, MedicoVista::class.java)
        startActivity(intent)
        finish() // Finaliza la actividad actual
    }
}

@Composable
fun MedicoAtenderScreen(pacienteNombre: String?, onBack: () -> Unit) {
    // Lista de exámenes disponibles
    val examenesDisponibles = listOf("Examen de Sangre", "Examen de Orina", "Radiografía", "Tomografía")
    val examenesSeleccionados = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Botón para regresar
        Button(onClick = onBack, modifier = Modifier.padding(bottom = 16.dp)) {
            Text("Regresar")
        }

        // Mostrar los datos del paciente
        Text("Datos del Paciente:", modifier = Modifier.padding(bottom = 8.dp))
        Text("Nombre: ${pacienteNombre}", modifier = Modifier.padding(bottom = 4.dp))

        // Mostrar opciones de exámenes
        Text("Seleccione los exámenes necesarios:", modifier = Modifier.padding(bottom = 8.dp))

        examenesDisponibles.forEach { examen ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                val isChecked = examenesSeleccionados.contains(examen)
                Checkbox(
                    checked = isChecked,
                    onCheckedChange = { isSelected ->
                        if (isSelected) {
                            examenesSeleccionados.add(examen)
                        } else {
                            examenesSeleccionados.remove(examen)
                        }
                    }
                )
                Text(text = examen, modifier = Modifier.padding(start = 8.dp))
            }
        }

        // Mostrar las selecciones realizadas
        if (examenesSeleccionados.isNotEmpty()) {
            Text(
                "Exámenes seleccionados:",
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            examenesSeleccionados.forEach { examen ->
                Text("- $examen")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de confirmación
        Button(
            onClick = { /* Lógica para procesar los exámenes seleccionados */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar Exámenes")
        }
        // Botones de Alta y Hospitalización
        Button(
            onClick = { /* Lógica para procesar los exámenes seleccionados */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Dar Alta")
        }
        Button(
            onClick = { /* Lógica para procesar los exámenes seleccionados */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hospitalizacion")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun MedicoAtenderPreview() {
    AppTheme {
        MedicoAtenderScreen(pacienteNombre = "Juan Pérez", onBack = { /* Lógica para volver atrás */ })
    }
}