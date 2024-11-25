package com.example.notisalud.Paramedico

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson

// Unified PacienteAccion data class
data class PacienteAccion(
    val id: String = "",  // Required for Firebase documents
    val nombres: String = "",
    val categorization: String = "",
    val atencion: String = "",
    val examenSolicitado: String = "",
    val atendido: Boolean = false,
    val examenRealizado: Boolean = false,
    val informeListo: Boolean = false
)

class ParamedicoVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ParamedicoVistaScreen { paciente ->
                    // Launch ParamedicoAccion activity with selected patient
                    val intent = Intent(this, ParamedicoAccion::class.java)
                    intent.putExtra("paciente", Gson().toJson(paciente))
                    startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun ParamedicoVistaScreen(onPacienteClick: (PacienteAccion) -> Unit) {
    var pacientes by remember { mutableStateOf<List<PacienteAccion>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Effect to fetch patients from Firebase
    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("pacientes")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    error = "Error al cargar pacientes: ${e.message}"
                    isLoading = false
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val pacientesList = snapshot.documents.mapNotNull { doc ->
                        try {
                            PacienteAccion(
                                id = doc.id,
                                nombres = doc.getString("nombres") ?: "",
                                categorization = doc.getString("categorization") ?: "",
                                atencion = doc.getString("atencion") ?: "",
                                examenSolicitado = doc.getString("examenSolicitado") ?: "",
                                atendido = doc.getBoolean("atendido") ?: false,
                                examenRealizado = doc.getBoolean("examenRealizado") ?: false,
                                informeListo = doc.getBoolean("informeListo") ?: false
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    pacientes = pacientesList
                    isLoading = false
                }
            }
    }

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

        when {
            isLoading -> {
                Text("Cargando pacientes...")
            }
            error != null -> {
                Text("Error: $error", color = androidx.compose.ui.graphics.Color.Red)
            }
            pacientes.isEmpty() -> {
                Text("No hay pacientes disponibles")
            }
            else -> {
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

                        // Add status indicators
                        Text(
                            text = "Categoría: ${paciente.categorization}",
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Atención: ${paciente.atencion}",
                            fontSize = 14.sp
                        )

                        // Status indicators
                        Row(
                            modifier = Modifier.padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatusIndicator("Atendido", paciente.atendido)
                            StatusIndicator("Examen", paciente.examenRealizado)
                            StatusIndicator("Informe", paciente.informeListo)
                        }

                        Button(
                            onClick = { onPacienteClick(paciente) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(text = "Ver Paciente")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusIndicator(label: String, isComplete: Boolean) {
    Text(
        text = "$label: ${if (isComplete) "✓" else "○"}",
        color = if (isComplete)
            androidx.compose.ui.graphics.Color.Green
        else
            androidx.compose.ui.graphics.Color.Gray,
        fontSize = 12.sp
    )
}