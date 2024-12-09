package com.example.notisalud.Medico

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class MedicoPacienteCheck : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userId = intent.getStringExtra("userId") ?: ""
        val nombre = intent.getStringExtra("nombre") ?: "Paciente Desconocido"
        val problemaSalud = intent.getStringExtra("problemaSalud") ?: "Sin descripción"
        val fiebre = intent.getStringExtra("fiebre") ?: "No aplica"
        val alergia = intent.getStringExtra("alergia") ?: "No aplica"
        val categorizacion = intent.getStringExtra("categorizacion") ?: "No categorizado"

        setContent {
            AppTheme {
                MedicoPacienteCheckScreen(
                    userId = userId,
                    nombre = nombre,
                    problemaSalud = problemaSalud,
                    fiebre = fiebre,
                    alergia = alergia,
                    categorizacion = categorizacion
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicoPacienteCheckScreen(
    userId: String,
    nombre: String,
    problemaSalud: String,
    fiebre: String,
    alergia: String,
    categorizacion: String
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var examenTexto by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Paciente") },
                navigationIcon = {
                    IconButton(onClick = { /* Acción para regresar */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text("Nombre: $nombre")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Problema de Salud: $problemaSalud")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Fiebre: $fiebre")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Alergias: $alergia")
            Spacer(modifier = Modifier.height(8.dp))
            Text("Categorización: $categorizacion")

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = examenTexto,
                onValueChange = { examenTexto = it },
                label = { Text("Tipo de Examen") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val examen = examenTexto.trim()
                    if (examen.isNotEmpty()) {
                        val examenData = hashMapOf(
                            "Examen" to examen,
                            "Fecha" to SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
                        )

                        db.collection("Users")
                            .document(userId)
                            .collection("Examenes")
                            .add(examenData)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Examen registrado exitosamente.", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Error al registrar el examen.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Por favor, ingrese un tipo de examen.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Petición de Examen")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Motivo de Alta") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    Toast.makeText(context, "Paciente dado de alta.", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dar de Alta")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewMedicoPacienteCheckScreen() {
    AppTheme {
        MedicoPacienteCheckScreen(
            userId = "",
            nombre = "Fernando Varas",
            problemaSalud = "Fractura de Rodilla",
            fiebre = "No aplica",
            alergia = "Paracetamol",
            categorizacion = "Atención General"
        )
    }
}
