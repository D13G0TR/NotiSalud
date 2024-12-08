package com.example.notisalud.Medico

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.tooling.preview.Preview

class MedicoPacienteCheck : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener los datos del paciente del intent
        val pacienteId = intent.getStringExtra("pacienteId") ?: ""
        val pacienteNombre = intent.getStringExtra("pacienteNombre") ?: "Paciente Desconocido"
        val problemaSalud = intent.getStringExtra("problemaSalud") ?: "Sin descripción"
        val detallesFiebre = intent.getStringExtra("detallesFiebre") ?: "No aplica"
        val detallesAlergia = intent.getStringExtra("detallesAlergia") ?: "No aplica"
        val categorizacion = intent.getStringExtra("categorizacion") ?: "No categorizado"

        setContent {
            AppTheme {
                MedicoPacienteCheckScreen(
                    pacienteId = pacienteId,
                    pacienteNombre = pacienteNombre,
                    problemaSalud = problemaSalud,
                    detallesFiebre = detallesFiebre,
                    detallesAlergia = detallesAlergia,
                    categorizacion = categorizacion,
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicoPacienteCheckScreen(
    pacienteId: String,
    pacienteNombre: String,
    problemaSalud: String,
    detallesFiebre: String,
    detallesAlergia: String,
    categorizacion: String,
    onBackPressed: () -> Unit
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    var motivoAlta by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Paciente") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Nombre: $pacienteNombre", style = MaterialTheme.typography.bodyLarge)
                Text("Problema de Salud: $problemaSalud", style = MaterialTheme.typography.bodyLarge)
                Text("Fiebre: $detallesFiebre", style = MaterialTheme.typography.bodyLarge)
                Text("Alergias: $detallesAlergia", style = MaterialTheme.typography.bodyLarge)
                Text("Categorización: $categorizacion", style = MaterialTheme.typography.bodyLarge)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        Toast.makeText(context, "Petición de examen no implementada aún", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Text("Petición de Examen")
                }

                OutlinedTextField(
                    value = motivoAlta,
                    onValueChange = { motivoAlta = it },
                    label = { Text("Motivo de Alta") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                )

                Button(
                    onClick = {
                        if (motivoAlta.isNotBlank()) {
                            db.collection("Users")
                                .document(pacienteId)
                                .collection("problemasDeSalud")
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    val problemaId = querySnapshot.documents.firstOrNull()?.id
                                    if (problemaId != null) {
                                        db.collection("Users")
                                            .document(pacienteId)
                                            .collection("problemasDeSalud")
                                            .document(problemaId)
                                            .update("estado", "Alta", "motivoAlta", motivoAlta)
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Paciente dado de alta", Toast.LENGTH_SHORT).show()
                                                onBackPressed()
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(context, "Error al dar de alta", Toast.LENGTH_SHORT).show()
                                            }
                                    } else {
                                        Toast.makeText(context, "Problema no encontrado", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        } else {
                            Toast.makeText(context, "Debe ingresar un motivo para el alta", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Dar de Alta")
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun MedicoPacienteCheckPreview() {
    AppTheme {
        MedicoPacienteCheckScreen(
            pacienteId = "mockId",
            pacienteNombre = "Juan Perez",
            problemaSalud = "Dolor de cabeza severo",
            detallesFiebre = "3 días",
            detallesAlergia = "Paracetamol",
            categorizacion = "Alta Urgencia",
            onBackPressed = {}
        )
    }
}
