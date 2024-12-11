package com.example.notisalud.Paciente

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PacienteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    PacienteScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth(),
                        onEnviar = { descripcion, tieneFiebre, duracionFiebre, tieneAlergia, detallesAlergia ->
                            enviarProblemaDeSalud(
                                descripcion,
                                tieneFiebre,
                                duracionFiebre,
                                tieneAlergia,
                                detallesAlergia
                            )
                        }
                    )
                }
            }
        }
    }

    private fun enviarProblemaDeSalud(
        descripcion: String,
        tieneFiebre: Boolean,
        duracionFiebre: String?,
        tieneAlergia: Boolean,
        detallesAlergia: String?
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            val problemaSalud = hashMapOf(
                "descripcion" to descripcion,
                "tieneFiebre" to tieneFiebre,
                "duracionFiebre" to (duracionFiebre ?: "No aplica"),
                "tieneAlergia" to tieneAlergia,
                "detallesAlergia" to (detallesAlergia ?: "No aplica"),
                "Categorizacion" to "pendiente",
                "timestamp" to com.google.firebase.Timestamp.now()
            )

            FirebaseFirestore.getInstance()
                .collection("Users")
                .document(userId)
                .collection("problemasDeSalud")
                .add(problemaSalud)
                .addOnSuccessListener {
                    Toast.makeText(
                        this,
                        "Problema de salud enviado exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    enviarNotificacionParaEnfermeros()
                    finish()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(
                        this,
                        "Error al enviar: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        } else {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun enviarNotificacionParaEnfermeros() {
        val db = FirebaseFirestore.getInstance()

        // Obtener todos los usuarios con el rol 'Enfermero'
        db.collection("Users")
            .whereEqualTo("rol", "Enfermero")
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Recorremos cada enfermero encontrado
                querySnapshot.documents.forEach { userDoc ->
                    val userId = userDoc.id

                    // Guarda la notificación en la subcolección 'notifications' de enfermero
                    db.collection("Users")
                        .document(userId)
                        .collection("notifications")
                        .add(
                            mapOf(
                                "title" to "Nuevo paciente",
                                "body" to "Un nuevo paciente necesita categorización.",
                                "timestamp" to com.google.firebase.Timestamp.now()
                            )
                        )
                        .addOnSuccessListener {
                            println("Notificación enviada correctamente a enfermero $userId")
                        }
                        .addOnFailureListener { e ->
                            println("Error al enviar la notificación: ${e.message}")
                        }
                }
            }
            .addOnFailureListener { e ->
                println("Error al obtener los enfermeros: ${e.message}")
            }
    }
}

    @Composable
fun PacienteScreen(
    modifier: Modifier = Modifier,
    onEnviar: (String, Boolean, String?, Boolean, String?) -> Unit
) {
    var descripcion by remember { mutableStateOf("") }
    var tieneFiebre by remember { mutableStateOf(false) }
    var duracionFiebre by remember { mutableStateOf("") }
    var tieneAlergia by remember { mutableStateOf(false) }
    var detallesAlergia by remember { mutableStateOf("") }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Describe tu problema de salud") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿Tiene fiebre?", modifier = Modifier.weight(1f))
            Switch(
                checked = tieneFiebre,
                onCheckedChange = { tieneFiebre = it }
            )
        }

        if (tieneFiebre) {
            TextField(
                value = duracionFiebre,
                onValueChange = { duracionFiebre = it },
                label = { Text("¿Cuánto tiempo lleva con fiebre?") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿Es alérgico a algo?", modifier = Modifier.weight(1f))
            Switch(
                checked = tieneAlergia,
                onCheckedChange = { tieneAlergia = it }
            )
        }

        if (tieneAlergia) {
            TextField(
                value = detallesAlergia,
                onValueChange = { detallesAlergia = it },
                label = { Text("Describa la alergia") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                onEnviar(
                    descripcion,
                    tieneFiebre,
                    duracionFiebre.ifBlank { null },
                    tieneAlergia,
                    detallesAlergia.ifBlank { null }
                )
            },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Enviar")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PacienteScreenPreview() {
    AppTheme {
        PacienteScreen(
            onEnviar = { _, _, _, _, _ -> }
        )
    }
}