package com.example.notisalud.Medico

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore

class DoctorVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                var pacientes by remember { mutableStateOf<List<PacienteUrgencia>>(emptyList()) }
                var isLoading by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    FirebaseFirestore.getInstance().collection("pacientes")
                        .whereEqualTo("estado", "validado")
                        .addSnapshotListener { snapshot, e ->
                            if (e != null) {
                                isLoading = false
                                return@addSnapshotListener
                            }
                            pacientes = snapshot?.documents?.mapNotNull { doc ->
                                PacienteUrgencia(
                                    id = doc.id,
                                    nombre = doc.getString("nombre") ?: "",
                                    urgencia = doc.getString("urgencia") ?: "",
                                    problemaSalud = doc.getString("problemaSalud") ?: "",
                                    fiebre = doc.getString("fiebre") ?: "",
                                    alergia = doc.getString("alergia") ?: "",
                                    validado = true
                                )
                            } ?: emptyList()
                            isLoading = false
                        }
                }

                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    } else {
                        TablaPacienteUrgenciaScreen(pacientes)
                    }
                }
            }
        }
    }
    @Composable
    fun TablaPacienteUrgenciaScreen(
        pacienteUrgencia: List<PacienteUrgencia>,
        modifier: Modifier = Modifier
    ) {
        val context = LocalContext.current

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "Lista de Pacientes con Urgencias:",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            if (pacienteUrgencia.isEmpty()) {
                Text("No hay pacientes disponibles.", style = MaterialTheme.typography.bodyLarge)
            } else {
                pacienteUrgencia.forEach { paciente ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Nombre: ${paciente.nombre}")
                            Text("Urgencia: ${paciente.urgencia}")

                            Button(
                                onClick = {
                                    try {
                                        val intent = Intent(context, MedicoAtender::class.java)
                                        intent.putExtra("paciente", paciente)
                                        context.startActivity(intent)
                                    } catch (e: Exception) {
                                        // Agregar log para depuración
                                        Log.e("MedicoAtender", "Error al iniciar actividad: ${e.message}")
                                        Toast.makeText(context, "Error al abrir la pantalla", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.align(Alignment.End)
                            ) {
                                Text("Atender")
                            }
                        }
                    }
                }
            }
        }
    }
}