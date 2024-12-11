package com.example.notisalud.Medico

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FieldValue
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
        val Categorizacion = intent.getStringExtra("Categorizacion") ?: "No categorizado"

        setContent {
            AppTheme {
                MedicoPacienteCheckScreen(
                    userId = userId,
                    nombre = nombre,
                    problemaSalud = problemaSalud,
                    fiebre = fiebre,
                    alergia = alergia,
                    Categorizacion = Categorizacion
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
    Categorizacion: String
) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()

    var examenOrina by remember { mutableStateOf(false) }
    var examenSangre by remember { mutableStateOf(false) }
    var motivoAlta by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Paciente") },
                navigationIcon = {
                    IconButton(onClick = {
                        (context as? Activity)?.onBackPressed()
                    }) {
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
            Text("Categorización: $Categorizacion")

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = examenOrina,
                    onCheckedChange = { examenOrina = it }
                )
                Text("Examen de Orina")
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = examenSangre,
                    onCheckedChange = { examenSangre = it }
                )
                Text("Examen de Sangre")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val examenes = mutableListOf<String>()
                    if (examenOrina) examenes.add("Examen de Orina")
                    if (examenSangre) examenes.add("Examen de Sangre")

                    if (examenes.isNotEmpty()) {
                        val examenesTexto = examenes.joinToString(", ")
                        val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                        db.collection("Users")
                            .document(userId)
                            .collection("problemasDeSalud")
                            .whereEqualTo("descripcion", problemaSalud)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                if (!querySnapshot.isEmpty) {
                                    val documentId = querySnapshot.documents[0].id
                                    val updateData: Map<String, Any> = mapOf(
                                        "Examenes" to FieldValue.arrayUnion(examenesTexto),
                                        "FechaExamenes" to FieldValue.arrayUnion(fechaActual)
                                    )

                                    db.collection("Users")
                                        .document(userId)
                                        .collection("problemasDeSalud")
                                        .document(documentId)
                                        .update(updateData)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Exámenes registrados exitosamente.", Toast.LENGTH_SHORT).show()
                                            examenOrina = false
                                            examenSangre = false
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Error al registrar los exámenes.", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(context, "No se encontró el problema de salud.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Error al buscar el problema de salud.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Seleccione al menos un examen.", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Petición de Examen")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = motivoAlta,
                onValueChange = { motivoAlta = it },
                label = { Text("Motivo de Alta") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (motivoAlta.isNotEmpty()) {
                        db.collection("Users")
                            .document(userId)
                            .collection("problemasDeSalud")
                            .whereEqualTo("descripcion", problemaSalud)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                if (!querySnapshot.isEmpty) {
                                    val documentId = querySnapshot.documents[0].id
                                    val updateData: Map<String, Any> = mapOf(
                                        "MotivoAlta" to motivoAlta,
                                        "FechaAlta" to SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                                        "EstadoAlta" to true
                                    )

                                    db.collection("Users")
                                        .document(userId)
                                        .collection("problemasDeSalud")
                                        .document(documentId)
                                        .update(updateData)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Paciente dado de alta.", Toast.LENGTH_SHORT).show()
                                            motivoAlta = ""
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(context, "Error al dar de alta.", Toast.LENGTH_SHORT).show()
                                        }
                                } else {
                                    Toast.makeText(context, "No se encontró el problema de salud.", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Error al buscar el problema de salud.", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        Toast.makeText(context, "Ingrese un motivo de alta.", Toast.LENGTH_SHORT).show()
                    }
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
            Categorizacion = "Atención General"
        )
    }
}