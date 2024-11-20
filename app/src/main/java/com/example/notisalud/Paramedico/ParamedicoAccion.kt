package com.example.notisalud.Paramedico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notisalud.ui.theme.AppTheme

// Datos del paciente con información adicional
data class PacienteAccion(
    val nombres: String,
    val categorization: String,
    val atencion: String,
    val examenSolicitado: String,
)

val pacienteAccion = PacienteAccion(
    nombres = "Juan Pérez",
    categorization = "Emergencia",
    atencion = "Urgencias",
    examenSolicitado = "Examen de Sangre"
)

class ParamedicoAccion : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ParamedicoAccionScreen(paciente = pacienteAccion)
            }
        }
    }
}

@Composable
fun ParamedicoAccionScreen(paciente: PacienteAccion) {
    // Inicialización de MutableState para los checkboxes
    val atendido: MutableState<Boolean> = remember { androidx.compose.runtime.mutableStateOf(false) }
    val examenRealizado: MutableState<Boolean> = remember { androidx.compose.runtime.mutableStateOf(false) }
    val informeListo: MutableState<Boolean> = remember { androidx.compose.runtime.mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Atendiendo a: ${paciente.nombres}",
            fontSize = 20.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Mostrar la información del paciente de forma sencilla
        Text(text = "Categoría: ${paciente.categorization}", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Atención: ${paciente.atencion}", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Examen solicitado: ${paciente.examenSolicitado}", fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Checkboxes para marcar el estado de las acciones
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = atendido.value,
                onCheckedChange = { atendido.value = it }
            )
            Text(text = "Paciente atendido", fontSize = 16.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = examenRealizado.value,
                onCheckedChange = { examenRealizado.value = it }
            )
            Text(text = "Examen realizado", fontSize = 16.sp)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = informeListo.value,
                onCheckedChange = { informeListo.value = it }
            )
            Text(text = "Informe listo", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón para guardar el estado del paciente
        Button(
            onClick = { onGuardarEstadoClick(atendido.value, examenRealizado.value, informeListo.value) },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Guardar estado del paciente")
        }

        // Botón para regresar a la vista de paramédico
        Button(
            onClick = { /* Aquí implementar la acción para regresar a la pantalla anterior */ },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Regresar")
        }
    }
}

// Función para guardar el estado del paciente (simulada)
fun onGuardarEstadoClick(atendido: Boolean, examenRealizado: Boolean, informeListo: Boolean) {
    println("Estado actualizado: Atendido: $atendido, Examen Realizado: $examenRealizado, Informe Listo: $informeListo")
}

@Preview(showSystemUi = true)
@Composable
fun ParamedicoAccionPreview() {
    AppTheme {
        ParamedicoAccionScreen(paciente = pacienteAccion)
    }
}