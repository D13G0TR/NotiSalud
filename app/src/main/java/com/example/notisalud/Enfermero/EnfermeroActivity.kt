package com.example.notisalud.Enfermero

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notisalud.Medico.MedicoAtender
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.runtime.Composable
import com.example.notisalud.Medico.PacienteUrgencia

class EnfermeroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Recuperar el paciente del intent
        val paciente = intent.getParcelableExtra<Paciente>("paciente")
            ?: throw IllegalStateException("No se recibieron datos del paciente")

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    EnfermeroScreen(
                        paciente = paciente,
                        onBackPressed = { finish() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnfermeroScreen(
    paciente: Paciente,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    var selectedUrgencia by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categorización de Paciente") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Text("<") // Simplified back button for this example
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información del paciente
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Información del Paciente",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Nombre: ${paciente.nombre}")
                    Text("Problema de salud: ${paciente.problemaSalud}")
                    Text("Fiebre: ${paciente.fiebre}")
                    Text("Alergias: ${paciente.alergia}")
                }
            }

            // Sección de categorización
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Categorizar Urgencia",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    val opcionesUrgencia = listOf(
                        "Atención General" to "Consultas de rutina, sin riesgo inmediato",
                        "Leve" to "Síntomas menores, puede esperar",
                        "Mediana gravedad" to "Requiere atención pronta pero no inmediata",
                        "Grave" to "Requiere atención inmediata",
                        "Riesgo vital" to "Emergencia, atención inmediata"
                    )

                    opcionesUrgencia.forEach { (opcion, descripcion) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedUrgencia == opcion,
                                onClick = { selectedUrgencia = opcion }
                            )
                            Column(
                                modifier = Modifier
                                    .padding(start = 8.dp)
                                    .weight(1f)
                            ) {
                                Text(
                                    text = opcion,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = descripcion,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Botón de validación
            Button(
                onClick = {
                    if (selectedUrgencia == null) {
                        Toast.makeText(
                            context,
                            "Por favor seleccione un nivel de urgencia",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    isLoading = true
                    // EnfermeroActivity
                    FirebaseFirestore.getInstance()
                        .collection("pacientes")
                        .document(paciente.id)
                        .update(
                            mapOf(
                                "estado" to "validado",
                                "urgencia" to selectedUrgencia
                            )
                        )
                        .addOnSuccessListener {
                            // Crear un objeto PacienteUrgencias a partir de Paciente
                            val pacienteUrgencias = PacienteUrgencia(
                                id = paciente.id,
                                nombre = paciente.nombre,
                                problemaSalud = paciente.problemaSalud,
                                urgencia = selectedUrgencia ?: "No especificada",
                                fiebre = paciente.fiebre,
                                alergia = paciente.alergia,
                                validado = true
                            )

                            // Enviar al médico
                            val intent = Intent(context, MedicoAtender::class.java)
                            intent.putExtra("paciente", pacienteUrgencias)
                            context.startActivity(intent)

                            onBackPressed()
                        }

                        .addOnFailureListener { e ->
                            Toast.makeText(
                                context,
                                "Error al categorizar: ${e.message}",
                                Toast.LENGTH_LONG
                            ).show()
                            isLoading = false
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Validar y Categorizar Paciente")
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun EnfermeroScreenPreview() {
    AppTheme {
        EnfermeroScreen(
            paciente = Paciente(
                id = "1",
                nombre = "Juan Pérez",
                problemaSalud = "Dolor de cabeza intenso",
                fiebre = "1 Dia",
                alergia = "Ninguna"
            ),
            onBackPressed = {}
        )
    }
}