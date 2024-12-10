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

data class PacienteParamedico(
    val id: String,
    val nombreCompleto: String
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
                                val nombreCompleto = "${userDoc.getString("nombre")} ${userDoc.getString("apellido")}"
                                listaPacientes.add(PacienteParamedico(userDoc.id, nombreCompleto))
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
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(pacientes) { paciente ->
                PacienteItem(paciente)
            }
        }
    }
}

@Composable
fun PacienteItem(paciente: PacienteParamedico) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Nombre: ${paciente.nombreCompleto}")
        }
    }
}
