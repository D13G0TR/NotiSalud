package com.example.notisalud.Medico

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme


class MedicoPacienteCheck : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nombre = intent.getStringExtra("nombre") ?: "Paciente Desconocido"
        val problemaSalud = intent.getStringExtra("problemaSalud") ?: "Sin descripción"
        val fiebre = intent.getStringExtra("fiebre") ?: "No aplica"
        val alergia = intent.getStringExtra("alergia") ?: "No aplica"
        val categorizacion = intent.getStringExtra("categorizacion") ?: "No categorizado"

        setContent {
            AppTheme {
                MedicoPacienteCheckScreen(
                    nombre = nombre,
                    problemaSalud = problemaSalud,
                    fiebre = fiebre,
                    alergia = alergia,
                    categorizacion = categorizacion
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun MedicoPacienteCheckScreen(
    nombre: String,
    problemaSalud: String,
    fiebre: String,
    alergia: String,
    categorizacion: String
) {
    val context = LocalContext.current // Contexto obtenido correctamente dentro de @Composable

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Paciente") },
                navigationIcon = {
                    IconButton(onClick = { /* Acción para regresar */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Nombre: $nombre")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Problema de Salud: $problemaSalud")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Fiebre: $fiebre")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Alergias: $alergia")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Categorización: $categorizacion")

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Toast.makeText(
                        context,
                        "Petición de Examen enviada.",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Petición de Examen")
            }

            Spacer(modifier = Modifier.height(16.dp))

            var motivoAlta by remember { mutableStateOf("") }
            OutlinedTextField(
                value = motivoAlta,
                onValueChange = { motivoAlta = it },
                label = { Text("Motivo de Alta") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (motivoAlta.isNotBlank()) {
                        Toast.makeText(
                            context,
                            "Paciente dado de alta con motivo: $motivoAlta",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            context,
                            "Por favor, proporcione un motivo de alta.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dar de Alta")
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun PreviewMedicoPacienteCheckScreen() {
    AppTheme {
        MedicoPacienteCheckScreen(
            nombre = "ejemplo ejemplo",
            problemaSalud = "ejemplo salud",
            fiebre = "No aplica",
            alergia = "ejemploalergia",
            categorizacion = "ejemplocat"
        )
    }
}