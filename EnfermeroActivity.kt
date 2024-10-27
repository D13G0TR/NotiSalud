package com.example.notisalud_main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud_main.ui.theme.NotiSaludmainTheme

class EnfermeroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotiSaludmainTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    EnfermeroMainScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

// Clase para los datos del paciente
data class Paciente(val nombre: String, val problemaSalud: String, val alergia: String)

// Lista de pacientes de ejemplo
val pacientes = listOf(
    Paciente("Juan Pérez", "Fiebre alta por 3 días", "Ninguna"),
    Paciente("Ana López", "Dolor de cabeza", "Penicilina"),
    Paciente("Carlos Ramírez", "Dificultad para respirar", "Polen")
)

@Composable
fun EnfermeroMainScreen(modifier: Modifier = Modifier) {
    var pacienteSeleccionado by remember { mutableStateOf<Paciente?>(null) }

    if (pacienteSeleccionado == null) {
        // Mostrar la lista de pacientes si no se ha seleccionado ninguno
        ListaPacientes(pacientes = pacientes, onPacienteValidar = { paciente ->
            pacienteSeleccionado = paciente
        }, onPacienteAnular = { /* Acción para anular */ })
    } else {
        // Mostrar los detalles del paciente seleccionado
        DetallePacienteScreen(paciente = pacienteSeleccionado!!, onVolverClick = {
            pacienteSeleccionado = null
        })
    }
}

@Composable
fun ListaPacientes(
    pacientes: List<Paciente>,
    onPacienteValidar: (Paciente) -> Unit,
    onPacienteAnular: (Paciente) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Pacientes en Espera:", modifier = Modifier.padding(1.dp))

        // Fila de cada paciente
        pacientes.forEach { paciente ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Columna de nombre
                Text(text = paciente.nombre, modifier = Modifier.weight(1f))
                // Columna de botones
                Row(modifier = Modifier.weight(1.2f), horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = { onPacienteValidar(paciente) }) {
                        Text("Validar")
                    }
                    Button(onClick = { onPacienteAnular(paciente) }) {
                        Text("Anular")
                    }
                }
            }
        }
    }
}

@Composable
fun DetallePacienteScreen(paciente: Paciente, onVolverClick: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Información del paciente
        Text("Nombre del paciente: ${paciente.nombre}")


        Button(
            onClick = { /* Acción de validación */ },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Validar Paciente")
        }

        Button(
            onClick = onVolverClick,
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Volver a la lista")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun EnfermeroMainScreenPreview() {
    NotiSaludmainTheme {
        EnfermeroMainScreen()
    }
}