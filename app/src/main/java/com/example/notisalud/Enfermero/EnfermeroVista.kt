package com.example.notisalud.Enfermero

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Paciente(
    val id: String = "",
    val nombre: String,
    val problemaSalud: String,
    val alergia: String,
    val fiebre: String
) : Parcelable


class EnfermeroVista : ComponentActivity() {
    private lateinit var listenerRegistration: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                var pacientes by remember { mutableStateOf<List<Paciente>>(emptyList()) }

                listenerRegistration = FirebaseFirestore.getInstance()
                    .collection("pacientes")
                    .whereEqualTo("estado", "pendiente")
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            return@addSnapshotListener
                        }
                        if (snapshot != null) {
                            pacientes = snapshot.documents.map { doc ->
                                Paciente(
                                    id = doc.id,
                                    nombre = doc.getString("nombre") ?: "Desconocido",
                                    problemaSalud = doc.getString("problemaSalud") ?: "",
                                    fiebre = doc.getString("fiebre") ?: "",
                                    alergia = doc.getString("detallesAlergia") ?: "Ninguna"
                                )
                            }
                        }
                    }

                ListaPacientesScreen(pacientes = pacientes)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaPacientesScreen(pacientes: List<Paciente>) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lista de Pacientes") }
            )
        }
    ) { padding ->
        if (pacientes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No hay pacientes en espera")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(pacientes) { paciente ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = paciente.nombre,
                                modifier = Modifier.weight(1f)
                            )
                            Row(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val intent = Intent(context, EnfermeroActivity::class.java).apply {
                                            putExtra("paciente", paciente)
                                        }
                                        context.startActivity(intent)
                                    }
                                ) {
                                    Text("Validar")
                                }
                                Button(
                                    onClick = {
                                        FirebaseFirestore.getInstance()
                                            .collection("pacientes")
                                            .document(paciente.id)
                                            .delete()
                                            .addOnSuccessListener {
                                                Toast.makeText(
                                                    context,
                                                    "Solicitud anulada",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                            .addOnFailureListener { e ->
                                                Toast.makeText(
                                                    context,
                                                    "Error al anular: ${e.message}",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                    }
                                ) {
                                    Text("Anular")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}