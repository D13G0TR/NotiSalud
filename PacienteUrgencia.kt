package com.example.notisalud_main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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

class PacienteUrgencia : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotiSaludmainTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    PacienteUrgenciaScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun PacienteUrgenciaScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ingresar Problema de Salud",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Campo para describir el problema de salud
        var problemaSalud by remember { mutableStateOf("") }

        TextField(
            value = problemaSalud,
            onValueChange = { problemaSalud = it },
            label = { Text("Describe tu problema de salud") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        var tieneFiebre by remember { mutableStateOf(false) }
        var fiebreDuracion by remember { mutableStateOf("") }
        var alergicoAlgo by remember { mutableStateOf(false) }
        var detallesAlergia by remember { mutableStateOf("") }

        // Check para fiebre
        Text("¿Tiene fiebre?")
        Checkbox(
            checked = tieneFiebre,
            onCheckedChange = { tieneFiebre = it }
        )

        // Si tiene fiebre, preguntar la duración
        if (tieneFiebre) {
            TextField(
                value = fiebreDuracion,
                onValueChange = { fiebreDuracion = it },
                label = { Text("¿Cuánto tiempo lleva con fiebre? (en días)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        // Check para alergias
        Text("¿Es alérgico a algo?")
        Checkbox(
            checked = alergicoAlgo,
            onCheckedChange = { alergicoAlgo = it }
        )

        // Si es alérgico, pedir detalles
        if (alergicoAlgo) {
            TextField(
                value = detallesAlergia,
                onValueChange = { detallesAlergia = it },
                label = { Text("Describa la alergia") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = { /* aca se validaran los datos en la bd */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enviar")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PacienteUrgenciaScreenPreview() {
    NotiSaludmainTheme {
        PacienteUrgenciaScreen()
    }
}