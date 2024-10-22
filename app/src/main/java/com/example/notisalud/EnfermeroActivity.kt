package com.example.notisalud

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
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
import com.example.notisalud.ui.theme.AppTheme

@Composable
fun EnfermeroScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Información del paciente
        Text("Nombre del paciente: Juan Pérez")
        Text("Problema de salud: Fiebre alta por 3 días")
        Text("Alergia: Ninguna")

        // Título para la categorización
        Text("Categorizar urgencia", modifier = Modifier.padding(vertical = 16.dp))

        val opcionesUrgencia = listOf("Atención General", "Leve", "Mediana gravedad", "Grave", "Riesgo vital")
        var opcionSeleccionada by remember { mutableStateOf("") }

        opcionesUrgencia.forEach { opcion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = opcion == opcionSeleccionada,
                    onClick = { opcionSeleccionada = opcion }
                )
                Text(text = opcion, modifier = Modifier.padding(start = 8.dp))
            }
        }

        Button(
            onClick = { /* Acción de validación */ },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Validar Paciente")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun EnfermeroScreenPreview() {
    AppTheme {
        EnfermeroScreen()
    }
}

