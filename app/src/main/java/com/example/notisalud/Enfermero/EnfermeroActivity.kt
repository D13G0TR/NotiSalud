package com.example.notisalud.Enfermero

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
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

class EnfermeroActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    EnfermeroActivityScreen(
                        modifier = Modifier.padding(innerPadding),
                        context = this,
                        pacienteId = intent.getStringExtra("pacienteId") ?: "",
                        onCategoriaSeleccionada = { categoria ->
                            categorizarPaciente(intent.getStringExtra("pacienteId") ?: "", categoria)
                        }
                    )
                }
            }
        }
    }

    private fun categorizarPaciente(pacienteId: String, categoria: String) {
        // Aquí se realiza la lógica para categorizar al paciente en Firestore
        println("Paciente con ID: $pacienteId categorizado como $categoria")
    }
}

@Composable
fun EnfermeroActivityScreen(
    modifier: Modifier = Modifier,
    context: Context?,
    pacienteId: String,
    onCategoriaSeleccionada: (String) -> Unit
) {
    val nombreCompleto = remember { mutableStateOf("Nombre no disponible") }
    val edad = remember { mutableStateOf("Edad no disponible") }
    val descripcion = remember { mutableStateOf("Sin descripción") }
    val detallesFiebre = remember { mutableStateOf("No aplica") }
    val detallesAlergias = remember { mutableStateOf("No aplica") }

    LaunchedEffect(pacienteId) {
        val firestore = FirebaseFirestore.getInstance()

        // Recuperar datos del paciente desde la colección "Users"
        firestore.collection("Users").document(pacienteId).get()
            .addOnSuccessListener { document ->
                val nombre = document.getString("nombre") ?: ""
                val apellido = document.getString("apellido") ?: ""
                val edadPaciente = document.getString("edad") ?: "Edad no disponible"

                nombreCompleto.value = "$nombre $apellido"
                edad.value = edadPaciente
            }

        // Recuperar datos de la subcolección "problemasDeSalud"
        firestore.collection("Users").document(pacienteId)
            .collection("problemasDeSalud").get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val problema = querySnapshot.documents[0]
                    descripcion.value = problema.getString("descripcion") ?: "Sin descripción"

                    // Manejar detalles de fiebre
                    val tieneFiebre = problema.getBoolean("tieneFiebre") ?: false
                    detallesFiebre.value = if (tieneFiebre) {
                        problema.getString("duracionFiebre") ?: "Duración no especificada"
                    } else {
                        "No aplica"
                    }

                    // Manejar detalles de alergias
                    val tieneAlergia = problema.getBoolean("tieneAlergia") ?: false
                    detallesAlergias.value = if (tieneAlergia) {
                        problema.getString("detallesAlergia") ?: "Detalles no especificados"
                    } else {
                        "No aplica"
                    }
                }
            }
    }


    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        // Título
        Text(
            text = "Categorización del Paciente",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Información del paciente
        Text(text = "Nombre Completo: ${nombreCompleto.value}", modifier = Modifier.padding(bottom = 8.dp))
        Text(text = "Edad: ${edad.value}", modifier = Modifier.padding(bottom = 8.dp))
        Text(text = "Descripción: ${descripcion.value}", modifier = Modifier.padding(bottom = 8.dp))
        Text(text = "Duración de fiebre (días): ${detallesFiebre.value}", modifier = Modifier.padding(bottom = 8.dp))
        Text(text = "Alergias: ${detallesAlergias.value}", modifier = Modifier.padding(bottom = 8.dp))

        // Botones de categorización
        CategorizationButton("Atención General", "Condición estable") { onCategoriaSeleccionada("Atención General") }
        CategorizationButton("Leve", "Atención no urgente") { onCategoriaSeleccionada("Leve") }
        CategorizationButton("Mediana Gravedad", "Requiere atención pronta") { onCategoriaSeleccionada("Mediana Gravedad") }
        CategorizationButton("Grave", "Atención inmediata") { onCategoriaSeleccionada("Grave") }
        CategorizationButton("Riesgo Vital", "Urgencia extrema") { onCategoriaSeleccionada("Riesgo Vital") }
    }
}

@Composable
fun CategorizationButton(
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = titulo)
            Text(text = subtitulo)
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun EnfermeroActivityPreview() {
    AppTheme {
        EnfermeroActivityScreen(
            modifier = Modifier.padding(0.dp),
            context = null, // Contexto nulo para vista previa
            pacienteId = "mockId", // Mock ID
            onCategoriaSeleccionada = {}
        )
    }
}
