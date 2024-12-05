package com.example.notisalud.Enfermero

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore



class EnfermeroVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    EnfermeroVistaScreen(
                        modifier = Modifier.padding(innerPadding).fillMaxSize(),
                        onPacienteSelected = { pacienteId ->
                            val intent = Intent(this, EnfermeroActivity::class.java).apply {
                                putExtra("pacienteId", pacienteId)
                            }
                            startActivity(intent)
                        },
                        context = this // Pasar el contexto de la actividad
                    )
                }
            }
        }
    }
}

@Composable
fun EnfermeroVistaScreen(
    modifier: Modifier = Modifier,
    onPacienteSelected: (String) -> Unit,
    context: android.content.Context // Contexto de la actividad
) {
    var pacientes by remember { mutableStateOf<List<PacienteEnfermero>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Cargar pacientes desde Firestore
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance()
            .collection("Users")
            .whereEqualTo("rol", "Paciente")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val pacientesList = querySnapshot.documents.mapNotNull { document ->
                    val nombre = document.getString("nombre")
                    val apellido = document.getString("apellido")
                    val userId = document.id
                    if (nombre != null && apellido != null) {
                        PacienteEnfermero(userId, "$nombre $apellido")
                    } else {
                        null
                    }
                }
                pacientes = pacientesList
                isLoading = false
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    context, // Usamos el contexto de la actividad
                    "Error al cargar pacientes: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
                isLoading = false
            }
    }

    // Mostrar un indicador de carga o la lista de pacientes
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = modifier) {
            items(pacientes) { paciente ->
                PacienteItem(
                    paciente = paciente,
                    onClick = { onPacienteSelected(paciente.id) }
                )
            }
        }
    }
}

@Composable
fun PacienteItem(paciente: PacienteEnfermero, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = paciente.nombreCompleto,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Seleccionar paciente"
                )
            }
        }
    }
}

// Modelo de datos actualizado
data class PacienteEnfermero(
    val id: String,
    val nombreCompleto: String
)

@Preview(showSystemUi = true)
@Composable
fun EnfermeroVistaPreview() {
    AppTheme {
        EnfermeroVistaScreen(
            onPacienteSelected = {},
            context = android.content.ContextWrapper(null) // Simulación de contexto para vista previa
        )
    }
}
