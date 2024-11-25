package com.example.notisalud.Medico

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore

class MedicoPacienteVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                PacientesUrgenciasScreen()
            }
        }
    }
}

@Composable
fun PacientesUrgenciasScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()

    // Estado para almacenar la lista de pacientes
    var pacientesUrgencias by remember { mutableStateOf<List<PacienteUrgencia>>(emptyList()) }

    // Cargar datos desde Firestore
    LaunchedEffect(Unit) {
        firestore.collection("urgencias")
            .whereEqualTo("validado", true) // Solo pacientes validados
            .get()
            .addOnSuccessListener { result ->
                // Mapeo de los documentos de Firestore a objetos PacienteUrgencia
                val pacientes = result.documents.map { document ->
                    PacienteUrgencia(
                        id = document.id, // El ID de Firestore
                        nombre = document.getString("nombre") ?: "Sin Nombre",
                        urgencia = document.getString("urgencia") ?: "Sin Urgencia",
                        validado = document.getBoolean("validado") ?: false,
                        problemaSalud = document.getString("problemaSalud") ?: "",
                        fiebre = document.getString("fiebre") ?: "",
                        alergia = document.getString("alergia") ?: ""
                    )
                }
                pacientesUrgencias = pacientes
            }
            .addOnFailureListener { exception ->
                // Manejo de errores (puedes usar logs o mostrar un mensaje al usuario)
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Lista de Pacientes con Urgencias:", modifier = Modifier.padding(bottom = 8.dp))

        pacientesUrgencias.forEach { paciente ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = paciente.nombre, modifier = Modifier.weight(1f))
                Text(text = paciente.urgencia, modifier = Modifier.weight(1f))
                Text(text = if (paciente.validado) "Validado" else "Pendiente", modifier = Modifier.weight(1f))

                // Botón para atender al paciente
                Button(
                    onClick = {
                        val intent = Intent(context, MedicoAtender::class.java).apply {
                            putExtra("paciente", paciente)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text("Atender")
                }
            }
        }
    }
}