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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.QueryDocumentSnapshot

class EnfermeroVista : ComponentActivity() {
    var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                EnfermeroVistaScreen(
                    onPacienteSelected = { pacienteId ->
                        // Implementación existente de selección de paciente
                        FirebaseFirestore.getInstance()
                            .collection("Users")
                            .document(pacienteId)
                            .collection("problemasDeSalud")
                            .get()
                            .addOnSuccessListener { problemasSnapshot ->
                                val problema = problemasSnapshot.documents.firstOrNull()
                                val intent = Intent(this, EnfermeroActivity::class.java).apply {
                                    putExtra("nombreCompleto", problema?.getString("nombreCompleto"))
                                    putExtra("descripcion", problema?.getString("descripcion") ?: "No disponible")
                                    putExtra("detallesFiebre", problema?.getString("detallesFiebre") ?: "No aplica")
                                    putExtra("detallesAlergia", problema?.getString("detallesAlergia") ?: "No aplica")
                                }
                                startActivity(intent)
                            }
                    },
                    context = this
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detener el listener cuando la actividad se destruye
        listenerRegistration?.remove()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnfermeroVistaScreen(
    modifier: Modifier = Modifier,
    onPacienteSelected: (String) -> Unit,
    context: android.content.Context
) {
    var pacientes by remember { mutableStateOf<List<PacienteConProblema>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Función para obtener pacientes con problemas de salud en tiempo real
    fun fetchDataInTime() {
        isLoading = true
        val firestore = FirebaseFirestore.getInstance()

        // Limpiar el listener anterior si existe
        (context as? EnfermeroVista)?.listenerRegistration?.remove()

        // Nuevo listener para tiempo real
        val registration = firestore.collection("Users")
            .whereEqualTo("rol", "Paciente")
            .addSnapshotListener { usuariosSnapshot, e ->
                if (e != null) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    isLoading = false
                    return@addSnapshotListener
                }

                val pacientesList = mutableListOf<PacienteConProblema>()

                usuariosSnapshot?.documents?.forEach { usuarioDoc ->
                    val userId = usuarioDoc.id
                    val nombre = usuarioDoc.getString("nombre")
                    val apellido = usuarioDoc.getString("apellido")

                    // Listener para problemas de salud de cada paciente
                    firestore.collection("Users")
                        .document(userId)
                        .collection("problemasDeSalud")
                        .addSnapshotListener { problemasSnapshot, problemError ->
                            if (problemError != null) {
                                Toast.makeText(context, "Error: ${problemError.message}", Toast.LENGTH_SHORT).show()
                                return@addSnapshotListener
                            }

                            if (!problemasSnapshot?.isEmpty!! == true && nombre != null && apellido != null) {
                                val ultimoProblema = problemasSnapshot?.documents?.lastOrNull()
                                val problema = ultimoProblema?.let { doc ->
                                    ProblemaDetalle(
                                        descripcion = doc.getString("descripcion") ?: "Sin descripción",
                                        tieneFiebre = doc.getBoolean("tieneFiebre") ?: false,
                                        duracionFiebre = doc.getString("duracionFiebre") ?: "No especificado",
                                        tieneAlergia = doc.getBoolean("tieneAlergia") ?: false,
                                        detallesAlergia = doc.getString("detallesAlergia") ?: "No especificado"
                                    )
                                }

                                // Verificar si ya existe el paciente para no duplicar
                                val existente = pacientesList.find { it.id == userId }
                                if (existente == null) {
                                    problema?.let {
                                        pacientesList.add(
                                            PacienteConProblema(
                                                id = userId,
                                                nombreCompleto = "$nombre $apellido",
                                                problema = it
                                            )
                                        )
                                    }
                                } else {
                                    // Actualizar problema existente
                                    problema?.let { existente.problema = it }
                                }

                                // Actualizar lista de pacientes
                                pacientes = pacientesList
                                isLoading = false
                            }
                        }
                }
            }

        // Guardar referencia al listener para poder detenerlo después
        (context as? EnfermeroVista)?.listenerRegistration = registration
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vista Enfermero") },
                actions = {
                    IconButton(onClick = { fetchDataInTime() }) {
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
                        PacienteConProblemaItem(
                            paciente = paciente,
                            onClick = { onPacienteSelected(paciente.id) }
                        )
                    }
                }
            }
        }
    }

    // Llamar a fetchDataInTime al cargar la pantalla
    LaunchedEffect(Unit) {
        fetchDataInTime()
    }
}

@Composable
fun PacienteConProblemaItem(paciente: PacienteConProblema, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
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

            // Mostrar detalles del problema de salud
            Text(
                text = "Descripción: ${paciente.problema.descripcion}",
                style = MaterialTheme.typography.bodyMedium
            )

            if (paciente.problema.tieneFiebre) {
                Text(
                    text = "Fiebre: Sí (Duración: ${paciente.problema.duracionFiebre})",
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (paciente.problema.tieneAlergia) {
                Text(
                    text = "Alergia: Sí (Detalles: ${paciente.problema.detallesAlergia})",
                    color = Color.Blue,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

data class PacienteConProblema(
    val id: String,
    val nombreCompleto: String,
    var problema: ProblemaDetalle
)

data class ProblemaDetalle(
    val descripcion: String,
    val tieneFiebre: Boolean,
    val duracionFiebre: String,
    val tieneAlergia: Boolean,
    val detallesAlergia: String
)

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