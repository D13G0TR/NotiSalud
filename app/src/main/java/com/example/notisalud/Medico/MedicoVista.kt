package com.example.notisalud.Medico

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import com.example.notisalud.ui.theme.AppTheme

// Datos del paciente
data class PacienteAtendido(val nombres: String)

val pacientesAtendidos = listOf(
    PacienteAtendido("Juan Pérez"),
    PacienteAtendido("Ana López"),
    PacienteAtendido("Carlos Ramírez")
)

class MedicoVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                TablaPacientesAtendidosScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun TablaPacientesAtendidosScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current // Obtiene el contexto de la Composable

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Lista de Pacientes:", modifier = Modifier.padding(bottom = 8.dp))

        // Tabla de pacientes
        pacientesAtendidos.forEach { paciente ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Columna de nombre
                Text(text = paciente.nombres, modifier = Modifier.weight(1f))

                // Columna de botones (Atender y Anular)
                Row(
                    modifier = Modifier
                        .weight(1.2f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { onAtenderClick(context, paciente) }) {
                        Text("Atender")
                    }
                    Button(onClick = { /* Acción de anular */ }) {
                        Text("Anular")
                    }
                }
            }
        }
    }
}

// Función para redirigir a la actividad MedicoAtender
fun onAtenderClick(context: android.content.Context, paciente: PacienteAtendido) {
    // Redirigir a MedicoAtender pasando los datos del paciente
    val intent = Intent(context, MedicoAtender::class.java)
    intent.putExtra("nombre_paciente", paciente.nombres) // se puede pasar más datos si es necesario
    context.startActivity(intent)
}

class MedicoAtenders : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuper el nombre del paciente desde el Intent
        val pacienteNombre = intent.getStringExtra("nombre_paciente") ?: "Paciente desconocido"

        setContent {
            AppTheme {
                MedicoAtenderScreen(pacienteNombre = pacienteNombre)
            }
        }
    }
}

@Composable
fun MedicoAtenderScreen(pacienteNombre: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Datos del Paciente:", modifier = Modifier.padding(bottom = 8.dp))
        Text("Nombre: $pacienteNombre", modifier = Modifier.padding(bottom = 8.dp))

        // Aquí agrego los controles adicionales, selección de exámenes, botones, etc.
        // Ejemplo de botón de confirmar
        Button(onClick = { /* Lógica de confirmación */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Confirmar Exámenes")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun TablaPacientesPreview() {
    AppTheme {
        TablaPacientesAtendidosScreen(modifier = Modifier.fillMaxSize())
    }
}