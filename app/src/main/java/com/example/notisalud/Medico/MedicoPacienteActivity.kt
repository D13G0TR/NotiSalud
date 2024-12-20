package com.example.notisalud.Medico

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
class MedicoPacienteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Pacientes Categorizados") },
                            navigationIcon = {
                                IconButton(onClick = {
                                    val intent = Intent(this, MedicoVista::class.java)
                                    startActivity(intent)
                                    finish()
                                }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                                }
                            }
                        )
                    }
                ) { innerPadding ->
                    PacienteListScreen(
                        modifier = Modifier.padding(innerPadding),
                        onPacienteSelected = { paciente ->
                            val intent = Intent(this, MedicoPacienteCheck::class.java).apply {
                                putExtra("userId", paciente.id)
                                putExtra("nombre", paciente.nombreCompleto)
                                putExtra("problemaSalud", paciente.problemaSalud)
                                putExtra("fiebre", paciente.fiebre)
                                putExtra("alergia", paciente.alergia)
                                putExtra("Categorizacion", paciente.Categorizacion)
                            }
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PacienteListScreen(
    modifier: Modifier = Modifier,
    onPacienteSelected: (PacienteCategorizado) -> Unit
) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    var pacientes by remember { mutableStateOf<List<PacienteCategorizado>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val pacientesRef = firestore.collection("Users")
        pacientesRef.addSnapshotListener { querySnapshot, error ->
            if (error != null) {
                Toast.makeText(context, "Error al escuchar cambios", Toast.LENGTH_SHORT).show()
                return@addSnapshotListener
            }

            val listaPacientes = mutableListOf<PacienteCategorizado>()

            querySnapshot?.documents?.forEach { userDocument ->
                val userId = userDocument.id
                val nombre = userDocument.getString("nombre") ?: "Desconocido"
                val apellido = userDocument.getString("apellido") ?: "Desconocido"

                userDocument.reference.collection("problemasDeSalud")
                    .whereNotEqualTo("Categorizacion", null)
                    .get()
                    .addOnSuccessListener { problemasSnapshot ->
                        problemasSnapshot.documents.forEach { problemaDoc ->
                            //erifica EstadoAlta no existe o es false
                            val estadoAlta = problemaDoc.getBoolean("EstadoAlta") ?: false
                            if (!estadoAlta) {
                                listaPacientes.add(
                                    PacienteCategorizado(
                                        id = userId,
                                        nombreCompleto = "$nombre $apellido",
                                        problemaSalud = problemaDoc.getString("descripcion") ?: "Sin descripción",
                                        fiebre = problemaDoc.getString("duracionFiebre") ?: "No aplica",
                                        alergia = problemaDoc.getString("detallesAlergia") ?: "No aplica",
                                        Categorizacion = problemaDoc.getString("Categorizacion") ?: "No categorizado"
                                    )
                                )
                            }
                        }
                        pacientes = listaPacientes
                        isLoading = false
                    }
                    .addOnFailureListener {
                        Log.e("Firestore", "Error al cargar problemas de salud", it)
                        Toast.makeText(context, "Error al cargar problemas de salud", Toast.LENGTH_SHORT).show()
                    }

            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(pacientes) { paciente ->
                PacienteItem(paciente = paciente, onClick = { onPacienteSelected(paciente) })
            }
        }
    }
}

@Composable
fun PacienteItem(paciente: PacienteCategorizado, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${paciente.nombreCompleto} - ${paciente.Categorizacion}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

data class PacienteCategorizado(
    val id: String,
    val nombreCompleto: String,
    val problemaSalud: String,
    val fiebre: String,
    val alergia: String,
    val Categorizacion: String
)

@Preview
@Composable
fun PreviewPacienteListScreen() {
    AppTheme {
        PacienteListScreen(onPacienteSelected = {})
    }
}