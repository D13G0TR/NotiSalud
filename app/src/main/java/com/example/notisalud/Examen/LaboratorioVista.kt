package com.example.notisalud.Examen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore

data class Examen(
    val pacienteId: String,
    val nombrePaciente: String,
    val tipoExamen: String,
    val estado: String
)

class LaboratorioVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                LaboratorioVistaScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun LaboratorioVistaScreen(modifier: Modifier = Modifier) {
    val db = FirebaseFirestore.getInstance()
    val exámenes = remember { mutableStateOf<List<Examen>>(emptyList()) }

    // Obtener los exámenes de laboratorio desde Firestore
    LaunchedEffect(true) {
        db.collection("examenesLaboratorio")
            .whereEqualTo("estado", "Pendiente")
            .get()
            .addOnSuccessListener { result ->
                exámenes.value = result.documents.map { document ->
                    Examen(
                        pacienteId = document.id, // Usando el ID del documento como pacienteId
                        nombrePaciente = document.getString("nombrePaciente") ?: "",
                        tipoExamen = document.getString("tipoExamen") ?: "",
                        estado = document.getString("estado") ?: "Pendiente"
                    )
                }
            }
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Text("Lista de Exámenes Pendientes")

        exámenes.value.forEach { examen ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(text = examen.nombrePaciente, modifier = Modifier.weight(1f))
                Text(text = examen.tipoExamen, modifier = Modifier.weight(1f))
                Button(
                    onClick = { onExamenListoClick(examen, db) },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Marcar como Listo")
                }
            }
        }
    }
}

fun onExamenListoClick(examen: Examen, db: FirebaseFirestore) {
    // Actualizar el estado del examen a "Listo" en Firestore usando el ID del documento
    db.collection("examenesLaboratorio")
        .document(examen.pacienteId) // Usar el ID de documento directamente
        .update("estado", "Listo")
        .addOnSuccessListener {
            // Acción después de actualizar el examen
            println("Examen de ${examen.nombrePaciente} marcado como listo.")
            // Podrías agregar un Toast aquí para dar retroalimentación al usuario
        }
        .addOnFailureListener {
            // Manejar el error si la actualización falla
            println("Error al actualizar el examen.")
        }
}