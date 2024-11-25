package com.example.notisalud.Medico

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown

class MedicoAtender : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener el paciente del intent
        val paciente = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("paciente", PacienteUrgencia::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra<PacienteUrgencia>("paciente")
        }

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Crear un paciente vacío si no hay datos
                    val pacienteDefault = PacienteUrgencia(
                        id = "",
                        nombre = "Nuevo Paciente",
                        urgencia = "",
                        problemaSalud = "",
                        fiebre = "",
                        alergia = "",
                        validado = false
                    )

                    MedicoAtenderScreen(
                        paciente = paciente ?: pacienteDefault,
                        onBackPressed = { finish() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicoAtenderScreen(paciente: PacienteUrgencia, onBackPressed: () -> Unit) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    var selectedExamen by remember { mutableStateOf<String?>(null) }
    val tiposExamen = listOf("Sangre", "Orina", "Radiografia", "Electrocardiograma")

    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Atención al Paciente") },
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text("Nombre: ${paciente.nombre}")
            Text("Problema: ${paciente.problemaSalud}")

            Spacer(modifier = Modifier.height(16.dp))

            ExamenSelector(
                tiposExamen = tiposExamen,
                selectedExamen = selectedExamen,
                onExamenSelected = { selectedExamen = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (selectedExamen != null) {
                        val examenData = hashMapOf(
                            "pacienteId" to paciente.id,
                            "nombrePaciente" to paciente.nombre,
                            "tipoExamen" to selectedExamen,
                            "estado" to "Pendiente"
                        )

                        val collection = if (selectedExamen == "Sangre" || selectedExamen == "Orina") {
                            db.collection("examenesLaboratorio")
                        } else {
                            db.collection("examenesRadiologia")
                        }

                        collection.add(examenData)
                            .addOnSuccessListener {
                                println("Datos enviados a ${collection.path}: $examenData")
                                Toast.makeText(context, "Examen solicitado exitosamente", Toast.LENGTH_SHORT).show()
                                onBackPressed() // Regresar
                            }
                            .addOnFailureListener { exception ->
                                println("Error al enviar datos: ${exception.message}")
                                Toast.makeText(context, "Error al solicitar examen", Toast.LENGTH_SHORT).show()
                            }

                    } else {
                        Toast.makeText(context, "Seleccione un tipo de examen", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Solicitar Examen")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isLoading = true
                    db.collection("pacientes").document(paciente.id)
                        .update("estado", "hospitalizado")
                        .addOnSuccessListener {
                            Toast.makeText(context, "Paciente hospitalizado", Toast.LENGTH_SHORT).show()
                            onBackPressed()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al actualizar", Toast.LENGTH_SHORT).show()
                            isLoading = false
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Hospitalizar")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    isLoading = true
                    db.collection("pacientes").document(paciente.id)
                        .update("estado", "alta")
                        .addOnSuccessListener {
                            Toast.makeText(context, "Paciente dado de alta", Toast.LENGTH_SHORT).show()
                            onBackPressed()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error al dar de alta", Toast.LENGTH_SHORT).show()
                            isLoading = false
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                Text("Dar de Alta")
            }
        }
    }
}

@Composable
fun ExamenSelector(
    tiposExamen: List<String>,
    selectedExamen: String?,
    onExamenSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        OutlinedTextField(
            value = selectedExamen ?: "Selecciona un examen",
            onValueChange = {},
            readOnly = true,
            label = { Text("Tipo de Examen") },
            trailingIcon = {
                Icon(
                    Icons.Default.ArrowDropDown,
                    contentDescription = "Abrir menú",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            tiposExamen.forEach { tipo ->
                DropdownMenuItem(
                    text = { Text(tipo) },
                    onClick = {
                        onExamenSelected(tipo)
                        expanded = false
                    }
                )
            }
        }
    }
}