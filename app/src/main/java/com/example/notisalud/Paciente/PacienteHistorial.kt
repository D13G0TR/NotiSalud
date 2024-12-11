package com.example.notisalud.Paciente

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.material3.ExperimentalMaterial3Api

data class Notificacion(
    val titulo: String,
    val mensaje: String,
    val fecha: String
)

@OptIn(ExperimentalMaterial3Api::class)
class PacienteHistorial : ComponentActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Recupera nombre y apellido del intent
        val firstName = intent.getStringExtra("firstName") ?: "Nombre"
        val lastName = intent.getStringExtra("lastName") ?: "Apellido"

        setContent {
            AppTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("Historial de Notificaciones") }
                        )
                    }
                ) { innerPadding ->
                    val notificaciones = remember { mutableStateOf(listOf<Notificacion>()) }

                    // Carga notificaciones cuando se inicia la actividad
                    LaunchedEffect(Unit) {
                        cargarNotificaciones(notificaciones)
                    }

                    HistorialNotificacionesContent(
                        modifier = Modifier.padding(innerPadding),
                        notificaciones = notificaciones.value
                    )
                }
            }
        }
    }

    private fun cargarNotificaciones(notificaciones: MutableState<List<Notificacion>>) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("Users")
                .document(userId)
                .collection("notificaciones")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val listaNotificaciones = querySnapshot.documents.mapNotNull { documento ->
                        Notificacion(
                            titulo = documento.getString("titulo") ?: "",
                            mensaje = documento.getString("mensaje") ?: "",
                            fecha = documento.getString("fecha") ?: ""
                        )
                    }
                    notificaciones.value = listaNotificaciones
                }
                .addOnFailureListener { exception ->
                    // Maneja error de carga de notificaciones
                    println("Error al cargar notificaciones: ${exception.message}")
                }
        }
    }
}

@Composable
fun HistorialNotificacionesContent(
    modifier: Modifier = Modifier,
    notificaciones: List<Notificacion>
) {
    if (notificaciones.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("No hay notificaciones")
        }
    } else {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(notificaciones) { notificacion ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = notificacion.titulo,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = notificacion.mensaje,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = notificacion.fecha,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}