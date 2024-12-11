package com.example.notisalud.Paramedico

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.delay

data class PacienteParamedico(
    val problemaId: String,
    val userId: String,
    val nombreCompleto: String,
    val examenes: List<String>
)

@OptIn(ExperimentalMaterial3Api::class)
class ParamedicoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Pacientes con Exámenes") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                                }
                            }
                        )
                    }
                ) { padding ->
                    ParamedicoListScreen(modifier = Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
fun ParamedicoListScreen(modifier: Modifier = Modifier) {
    val db = FirebaseFirestore.getInstance()
    var pacientes by remember { mutableStateOf<List<PacienteParamedico>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Cargar pacientes con el campo "Examenes"
    LaunchedEffect(Unit) {
        isLoading = true
        delay(3000)
        db.collection("Users")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val listaPacientes = mutableListOf<PacienteParamedico>()
                querySnapshot.documents.forEach { userDoc ->
                    userDoc.reference.collection("problemasDeSalud")
                        .whereNotEqualTo("Examenes", null)
                        .get()
                        .addOnSuccessListener { problemasSnapshot ->
                            problemasSnapshot.documents.forEach { problemaDoc ->
                                val examenes = problemaDoc["Examenes"] as? List<*>
                                val nombreCompleto = "${userDoc.getString("nombre")} ${userDoc.getString("apellido")}"
                                if (examenes != null && examenes.isNotEmpty() && problemaDoc["EstadodeExamen"] == null) {
                                    listaPacientes.add(
                                        PacienteParamedico(
                                            problemaId = problemaDoc.id,
                                            userId = userDoc.id,
                                            nombreCompleto = nombreCompleto,
                                            examenes = examenes.filterIsInstance<String>()
                                        )
                                    )
                                }
                            }
                            pacientes = listaPacientes
                            isLoading = false
                        }
                        .addOnFailureListener {
                            errorMessage = "Error al cargar los problemas de salud."
                            isLoading = false
                        }
                }
            }
            .addOnFailureListener {
                errorMessage = "Error al cargar los usuarios."
                isLoading = false
            }
    }

    if (isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (!errorMessage.isNullOrEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = errorMessage ?: "Error desconocido", color = MaterialTheme.colorScheme.error)
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize()
        ) {
            items(pacientes, key = { it.problemaId }) { paciente ->
                PacienteItem(
                    paciente = paciente,
                    onConfirm = { confirmadoPaciente ->
                        confirmarExamen(confirmadoPaciente, db, pacientes) { updatedList ->
                            pacientes = updatedList
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun PacienteItem(paciente: PacienteParamedico, onConfirm: (PacienteParamedico) -> Unit) {
    var isChecked by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Confirmación") },
            text = { Text("¿Desea confirmar el Examen para ${paciente.nombreCompleto}?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    isChecked = false
                    onConfirm(paciente)
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    isChecked = false
                }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("Nombre: ${paciente.nombreCompleto}")
                Spacer(modifier = Modifier.height(4.dp))
                paciente.examenes.forEach { examen ->
                    Text(
                        text = "Examen: $examen",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Checkbox(
                checked = isChecked,
                onCheckedChange = { checked ->
                    isChecked = checked
                    if (checked) {
                        showDialog = true
                    }
                }
            )
        }
    }
}


fun confirmarExamen(
    paciente: PacienteParamedico,
    db: FirebaseFirestore,
    pacientes: List<PacienteParamedico>,
    onExamenConfirmado: (List<PacienteParamedico>) -> Unit
) {
    // Crea el campo "EstadodeExamen" en Firestore
    db.collection("Users")
        .document(paciente.userId)
        .collection("problemasDeSalud")
        .document(paciente.problemaId)
        .set(mapOf("EstadodeExamen" to "EnEspera"), SetOptions.merge())
        .addOnSuccessListener {
            println("Campo 'EstadodeExamen' creado para: ${paciente.nombreCompleto}")
            // Filtra paciente del listado después de actualizar Firestore
            onExamenConfirmado(
                pacientes.filter { it.problemaId != paciente.problemaId }
            )
        }
        .addOnFailureListener { e ->
            println("Error al crear el campo 'EstadodeExamen': ${e.message}")
        }
}