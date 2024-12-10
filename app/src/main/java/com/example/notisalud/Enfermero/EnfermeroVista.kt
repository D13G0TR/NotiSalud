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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

class EnfermeroVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                EnfermeroVistaScreen(
                    onPacienteSelected = { pacienteId ->
                        FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(pacienteId)
                            .get()
                            .addOnSuccessListener { document ->
                                val nombre = document.getString("nombre")
                                val apellido = document.getString("apellido")

                                FirebaseFirestore.getInstance()
                                    .collection("Users")
                                    .document(pacienteId)
                                    .collection("problemasDeSalud")
                                    .get()
                                    .addOnSuccessListener { problemasSnapshot ->
                                        val problema = problemasSnapshot.documents.firstOrNull()
                                        val descripcion = problema?.getString("descripcion") ?: "No disponible"
                                        val detallesAlergia = problema?.getString("detallesAlergia") ?: "No aplica"
                                        val tieneAlergia = problema?.getBoolean("tieneAlergia") ?: false
                                        val tieneFiebre = problema?.getBoolean("tieneFiebre") ?: false
                                        val duracionFiebre = problema?.getString("duracionFiebre") ?: "0"
                                        val categorizacion = problema?.getString("Categorizacion") ?: "No especificado"

                                        val fiebreTexto = if (tieneFiebre) {
                                            "$duracionFiebre día(s)"
                                        } else {
                                            "No tiene fiebre"
                                        }

                                        val alergiaTexto = if (tieneAlergia) {
                                            "$detallesAlergia"
                                        } else {
                                            "No tiene alergias reportadas"
                                        }

                                        val intent = Intent(this, EnfermeroActivity::class.java).apply {
                                            putExtra("pacienteId", pacienteId)
                                            putExtra("descripcion", descripcion)
                                            putExtra("detallesFiebre", fiebreTexto)
                                            putExtra("detallesAlergia", alergiaTexto)
                                            putExtra("Categorizacion", categorizacion)
                                            putExtra("nombreCompleto", "$nombre $apellido")
                                        }
                                        startActivity(intent)
                                    }
                            }
                    },
                    context = this
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnfermeroVistaScreen(
    modifier: Modifier = Modifier,
    onPacienteSelected: (String) -> Unit,
    context: android.content.Context
) {
    var pacientes by remember { mutableStateOf<List<PacienteEnfermero>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    fun fetchData() {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("Users")
            .whereEqualTo("rol", "Paciente")
            .addSnapshotListener { querySnapshot, error ->
                if (error != null) {
                    Toast.makeText(
                        context,
                        "Error al cargar pacientes: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    isLoading = false
                    return@addSnapshotListener
                }

                val pacientesList = mutableListOf<PacienteEnfermero>()

                querySnapshot?.documents?.forEach { document ->
                    val userId = document.id
                    val nombre = document.getString("nombre")
                    val apellido = document.getString("apellido")

                    if (!nombre.isNullOrEmpty() && !apellido.isNullOrEmpty()) {
                        firestore.collection("Users")
                            .document(userId)
                            .collection("problemasDeSalud")
                            .whereEqualTo("Categorizacion", "pendiente")
                            .addSnapshotListener { problemasSnapshot, problemasError ->
                                if (problemasError != null) {
                                    Toast.makeText(
                                        context,
                                        "Error al cargar problemas de salud: ${problemasError.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    isLoading = false
                                    return@addSnapshotListener
                                }

                                if (problemasSnapshot != null && !problemasSnapshot.isEmpty) {
                                    pacientesList.add(PacienteEnfermero(userId, "$nombre $apellido"))
                                }

                                pacientes = pacientesList
                                isLoading = false
                            }
                    }
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vista Enfermero") },
                actions = {
                    IconButton(onClick = { fetchData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualizar")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(pacientes) { paciente ->
                        PacienteItem(paciente = paciente, onClick = { onPacienteSelected(paciente.id) })
                    }
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(3000) // Agregar retraso de 3 segundos
        fetchData()
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
                Icon(imageVector = Icons.Default.ArrowForward, contentDescription = "Seleccionar paciente")
            }
        }
    }
}

data class PacienteEnfermero(val id: String, val nombreCompleto: String)

@Preview(showSystemUi = true)
@Composable
fun EnfermeroVistaPreview() {
    AppTheme {
        EnfermeroVistaScreen(
            onPacienteSelected = {},
            context = android.content.ContextWrapper(null)
        )
    }
}
