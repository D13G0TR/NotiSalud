package com.example.notisalud.Enfermero

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

class EnfermeroVista : ComponentActivity() {
    private lateinit var listenerRegistration: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                var pacientes by remember { mutableStateOf<List<Paciente>>(emptyList()) }

                // Escuchar los datos en Firestore
                listenerRegistration = FirebaseFirestore.getInstance()
                    .collection("pacientes")
                    .whereEqualTo("estado", "pendiente")
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            return@addSnapshotListener
                        }
                        if (snapshot != null && !snapshot.isEmpty) {
                            pacientes = snapshot.documents.map { doc ->
                                Paciente(
                                    nombre = doc.getString("nombre") ?: "Desconocido",
                                    problemaSalud = doc.getString("problemaSalud") ?: "",
                                    alergia = doc.getString("detallesAlergia") ?: "Ninguna"
                                )
                            }
                        }
                    }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    EnfermeroVistaScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth(),
                        pacientes = pacientes
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration.remove() // Detener el listener para evitar fugas de memoria
    }

    // Clase para los datos del paciente
    data class Paciente(val nombre: String, val problemaSalud: String, val alergia: String)

    @Composable
    fun EnfermeroVistaScreen(
        modifier: Modifier = Modifier,
        pacientes: List<Paciente>
    ) {
        var pacienteSeleccionado by remember { mutableStateOf<Paciente?>(null) }

        if (pacienteSeleccionado == null) {
            ListaPacientes(pacientes = pacientes, onPacienteValidar = { paciente ->
                pacienteSeleccionado = paciente
            }, onPacienteAnular = { paciente ->
                // Aquí implementar lógica para "anular" el paciente
            })
        } else {
            DetallePacienteScreen(paciente = pacienteSeleccionado!!, onVolverClick = {
                pacienteSeleccionado = null
            })
        }
    }

    @Composable
    fun DetallePacienteScreen(paciente: Paciente, onVolverClick: () -> Unit) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Nombre del paciente: ${paciente.nombre}")
            Text("Problema de salud: ${paciente.problemaSalud}")
            Text("Alergias: ${paciente.alergia}")

            Button(
                onClick = {
                    FirebaseFirestore.getInstance()
                        .collection("pacientes")
                        .whereEqualTo("nombre", paciente.nombre) // Ajusta si tienes un ID único
                        .get()
                        .addOnSuccessListener { documents ->
                            for (document in documents) {
                                document.reference.update("estado", "validado")
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Validar Paciente")
            }

            Button(
                onClick = onVolverClick,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text("Volver a la lista")
            }
        }
    }

    @Composable
    fun ListaPacientes(
        pacientes: List<Paciente>,
        onPacienteValidar: (Paciente) -> Unit,
        onPacienteAnular: (Paciente) -> Unit
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text("Pacientes en Espera:", modifier = Modifier.padding(1.dp))

            // Fila de cada paciente
            pacientes.forEach { paciente ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Columna de nombre
                    Text(text = paciente.nombre, modifier = Modifier.weight(1f))
                    // Columna de botones
                    Row(modifier = Modifier.weight(1.2f), horizontalArrangement = Arrangement.SpaceBetween) {
                        Button(onClick = { onPacienteValidar(paciente) }) {
                            Text("Validar")
                        }
                        Button(onClick = { onPacienteAnular(paciente) }) {
                            Text("Anular")
                        }
                    }
                }
            }
        }
    }

    @Preview(showSystemUi = true)
    @Composable
    fun EnfermeroMainScreenPreview() {
        AppTheme {
            EnfermeroVistaScreen(pacientes = listOf())
        }
    }
}