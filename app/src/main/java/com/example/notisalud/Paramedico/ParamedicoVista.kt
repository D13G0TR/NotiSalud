package com.example.notisalud.Paramedico

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore

data class PacienteParamedico(
    val id: String,
    val nombreCompleto: String,
    val examenes: String,
    val problemaId: String
)

class ParamedicoVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ParamedicoVistaContent()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParamedicoVistaContent() {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    var pacientes by remember { mutableStateOf<List<PacienteParamedico>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var pacienteSeleccionado by remember { mutableStateOf<PacienteParamedico?>(null) }
    var mostrarDialogo by remember { mutableStateOf(false) }

    // Cargar datos
    LaunchedEffect(Unit) {
        db.collection("Users")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val pacientesConExamen = querySnapshot.documents.flatMap { userDoc ->
                    val userId = userDoc.id
                    userDoc.reference.collection("problemasDeSalud")
                        .get()
                        .result?.documents?.mapNotNull { problemaDoc ->
                            val examenes = problemaDoc.getString("Examenes")
                            val estadoExamen = problemaDoc.getString("EstadodeExamen")
                            if (examenes != null && estadoExamen == null) {
                                PacienteParamedico(
                                    id = userId,
                                    nombreCompleto = "${userDoc.getString("nombre")} ${userDoc.getString("apellido")}",
                                    examenes = examenes,
                                    problemaId = problemaDoc.id
                                )
                            } else null
                        } ?: emptyList()
                }
                pacientes = pacientesConExamen
                isLoading = false
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error al cargar pacientes.", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
    }

    if (mostrarDialogo) {
        pacienteSeleccionado?.let { paciente ->
            ConfirmarExamenDialog(
                paciente = paciente,
                onConfirm = {
                    db.collection("Users")
                        .document(paciente.id)
                        .collection("problemasDeSalud")
                        .document(paciente.problemaId)
                        .update("EstadodeExamen", "En Espera")
                        .addOnSuccessListener {
                            Toast.makeText(context, "Examen confirmado exitosamente.", Toast.LENGTH_SHORT).show()
                            pacientes = pacientes.filterNot { it.id == paciente.id }
                            mostrarDialogo = false
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al confirmar el examen.", Toast.LENGTH_SHORT).show()
                        }
                },
                onDismiss = { mostrarDialogo = false }
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paramédico - Confirmar Exámenes") }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
            ) {
                items(pacientes) { paciente ->
                    PacienteParamedicoItem(
                        paciente = paciente,
                        onConfirm = {
                            pacienteSeleccionado = paciente
                            mostrarDialogo = true
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PacienteParamedicoItem(
    paciente: PacienteParamedico,
    onConfirm: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nombre: ${paciente.nombreCompleto}")
            Text("Examen: ${paciente.examenes}")
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth()) {
                Text("Confirmar Examen")
            }
        }
    }
}

@Composable
fun ConfirmarExamenDialog(
    paciente: PacienteParamedico,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Confirmar Examen") },
        text = { Text("¿Estás seguro de confirmar el examen para este paciente?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Sí")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@Preview(showSystemUi = true)
@Composable
fun PreviewParamedicoVistaContent() {
    AppTheme {
        ParamedicoVistaContent()
    }
}
