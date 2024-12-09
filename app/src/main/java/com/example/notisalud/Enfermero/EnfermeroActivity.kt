package com.example.notisalud.Enfermero

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore

class EnfermeroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pacienteId = intent.getStringExtra("pacienteId") ?: ""
        val nombreCompleto = intent.getStringExtra("nombreCompleto") ?: "Nombre no disponible"
        val descripcion = intent.getStringExtra("descripcion") ?: "No disponible"
        val detallesFiebre = intent.getStringExtra("detallesFiebre") ?: "No aplica"
        val detallesAlergia = intent.getStringExtra("detallesAlergia") ?: "No aplica"

        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    EnfermeroActivityScreen(
                        modifier = Modifier.padding(innerPadding),
                        nombreCompleto = nombreCompleto,
                        descripcion = descripcion,
                        detallesFiebre = detallesFiebre,
                        detallesAlergia = detallesAlergia,
                        onCategorizacionSeleccionada = { categoria ->
                            actualizarCategorizacion(pacienteId, categoria)
                        }
                    )
                }
            }
        }
    }

    private fun actualizarCategorizacion(pacienteId: String, categoria: String) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("Users")
            .document(pacienteId)
            .collection("problemasDeSalud")
            .get()
            .addOnSuccessListener { problemasSnapshot ->
                val problemaId = problemasSnapshot.documents.firstOrNull()?.id
                if (problemaId != null) {
                    firestore.collection("Users")
                        .document(pacienteId)
                        .collection("problemasDeSalud")
                        .document(problemaId)
                        .update("Categorizacion", categoria)
                        .addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Categorización '$categoria' actualizada correctamente.",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(
                                this,
                                "Error al actualizar categorización: ${exception.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                } else {
                    Toast.makeText(this, "No se encontró el problema de salud.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@Composable
fun EnfermeroActivityScreen(
    modifier: Modifier = Modifier,
    nombreCompleto: String,
    descripcion: String,
    detallesFiebre: String,
    detallesAlergia: String,
    onCategorizacionSeleccionada: (String) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Detalles del Paciente", modifier = Modifier.padding(bottom = 16.dp))
        Text(text = "Nombre Completo: $nombreCompleto", modifier = Modifier.padding(bottom = 8.dp))
        Text(text = "Descripción: $descripcion", modifier = Modifier.padding(bottom = 8.dp))
        Text(text = "Detalles Fiebre: $detallesFiebre", modifier = Modifier.padding(bottom = 8.dp))
        Text(text = "Detalles Alergia: $detallesAlergia", modifier = Modifier.padding(bottom = 8.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Selecciona una categorización:", modifier = Modifier.padding(bottom = 16.dp))

        // Botones de categorización
        CategorizationButton("Atención General") { onCategorizacionSeleccionada("Atención General") }
        CategorizationButton("Leve") { onCategorizacionSeleccionada("Leve") }
        CategorizationButton("Mediana Gravedad") { onCategorizacionSeleccionada("Mediana Gravedad") }
        CategorizationButton("Grave") { onCategorizacionSeleccionada("Grave") }
        CategorizationButton("Riesgo Vital") { onCategorizacionSeleccionada("Riesgo Vital") }
    }
}

@Composable
fun CategorizationButton(
    categoria: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = categoria)
    }
}

@Preview(showSystemUi = true)
@Composable
fun EnfermeroActivityPreview() {
    AppTheme {
        EnfermeroActivityScreen(
            nombreCompleto = "Juan Pérez",
            descripcion = "Dolor de cabeza",
            detallesFiebre = "3 días",
            detallesAlergia = "Polen",
            onCategorizacionSeleccionada = {}
        )
    }
}