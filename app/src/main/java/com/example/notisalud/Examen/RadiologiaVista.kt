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

data class ExamenRadiologia(
    val pacienteId: String,
    val nombrePaciente: String,
    val tipoExamen: String,
    val estado: String
)

class RadiologiaVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                RadiologiaVistaScreen(
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun RadiologiaVistaScreen(modifier: Modifier = Modifier) {
    val db = FirebaseFirestore.getInstance()
    val exámenes = remember { mutableStateOf<List<ExamenRadiologia>>(emptyList()) }

    LaunchedEffect(Unit) {
        db.collection("examenesRadiologia")
            .whereEqualTo("estado", "Pendiente")
            .get()
            .addOnSuccessListener { result ->
                val lista = result.documents.map { doc ->
                    ExamenRadiologia(
                        pacienteId = doc.id, // Usar pacienteId correctamente
                        nombrePaciente = doc.getString("nombrePaciente") ?: "",
                        tipoExamen = doc.getString("tipoExamen") ?: "",
                        estado = doc.getString("estado") ?: ""
                    )
                }
                exámenes.value = lista
            }
            .addOnFailureListener {
                println("Error al obtener exámenes: ${it.message}")
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text("Lista de Pacientes para Exámenes de Radiología:", modifier = Modifier.padding(bottom = 8.dp))

        exámenes.value.forEach { examen ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = examen.nombrePaciente, modifier = Modifier.weight(1f))

                Text(text = examen.tipoExamen, modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.weight(1.2f),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Button(onClick = { onExamenListoClick(examen) }) {
                        Text("Radiología lista")
                    }
                }
            }
        }
    }
}

fun onExamenListoClick(examen: ExamenRadiologia) {

    val db = FirebaseFirestore.getInstance()
    db.collection("examenesRadiologia")
        .document(examen.pacienteId)
        .update("estado", "Listo")
        .addOnSuccessListener {

            println("Examen de ${examen.nombrePaciente} marcado como listo.")
        }
}

@Preview(showSystemUi = true)
@Composable
fun RadiologiaVistaPreview() {
    AppTheme {
        RadiologiaVistaScreen(modifier = Modifier.fillMaxSize())
    }
}