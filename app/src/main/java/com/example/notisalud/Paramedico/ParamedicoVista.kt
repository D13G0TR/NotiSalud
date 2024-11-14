package com.example.notisalud.Paramedico

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
import androidx.compose.ui.unit.sp
import com.example.notisalud.ui.theme.AppTheme

// Datos de un paciente (Ejemplo)
data class Paciente(
    val id: Int,
    val nombres: String,
    val categorization: String, // Categoría proporcionada por el enfermero
    val atencion: String, // Atención proporcionada por el médico
    val examenSolicitado: String // Examen solicitado por el médico
)

// Lista de pacientes de ejemplo
val pacientes = listOf(
    Paciente(1, "Juan Pérez", "Emergencia", "Urgencias", "Examen de Sangre"),
    Paciente(2, "Ana Gómez", "Consulta Programada", "Control Médico", "Electrocardiograma")
)

class ParamedicoVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ParamedicoVistaScreen()
            }
        }
    }
}

@Composable
fun ParamedicoVistaScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Lista de Pacientes para Atención",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Mostrar la lista de pacientes con un nombre y un botón de acción
        pacientes.forEach { paciente ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = paciente.nombres,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Button(
                    onClick = { /* Acción de navegación a la pantalla de acción del paramédico */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Ver Paciente")
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun ParamedicoVistaPreview() {
    AppTheme {
        ParamedicoVistaScreen()
    }
}