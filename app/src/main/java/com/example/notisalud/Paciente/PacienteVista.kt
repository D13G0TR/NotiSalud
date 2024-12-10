package com.example.notisalud.Paciente

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.example.notisalud.MainActivity
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PacienteVista : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializar Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Crear canal de notificaciones
        createNotificationChannel()

        // Recupera el nombre y apellido del intent
        val firstName = intent.getStringExtra("firstName") ?: "Nombre no disponible"
        val lastName = intent.getStringExtra("lastName") ?: "Apellido no disponible"

        // Configurar el listener para notificaciones en tiempo real
        val userId = auth.currentUser?.uid
        if (userId != null) {
            setupFirestoreListener(userId)
        }

        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    PacienteVistaScreen(
                        firstName = firstName,
                        lastName = lastName,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth(),
                        onUrgenciasClick = {
                            // Redirige a PacienteActivity
                            val intent = Intent(this, PacienteActivity::class.java).apply {
                                putExtra("firstName", firstName)
                                putExtra("lastName", lastName)
                            }
                            startActivity(intent)
                        },
                        onCloseSessionClick = {
                            // Cerrar sesión y redirigir a MainActivity
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }

    private fun setupFirestoreListener(userId: String) {
        firestore.collection("Users")
            .document(userId)
            .collection("problemasDeSalud")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    println("Error al escuchar cambios: ${error.message}")
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach { change ->
                    val data = change.document.data
                    val motivoAlta = data["MotivoAlta"] as? String
                    val fechaAlta = data["FechaAlta"] as? String

                    if (motivoAlta != null && fechaAlta != null) {
                        mostrarNotificacion(
                            "Alta Médica",
                            "Has sido dado de alta. Motivo: $motivoAlta. Fecha: $fechaAlta"
                        )
                    }
                }
            }
    }

    private fun mostrarNotificacion(titulo: String, mensaje: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = (System.currentTimeMillis() % 10000).toInt() // ID único para cada notificación
        val notification = NotificationCompat.Builder(this, "examenChannel")
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Usa un ícono apropiado
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "examenChannel",
                "Notificaciones de Examen y Alta Médica",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones cuando los exámenes están listos o los pacientes son dados de alta."
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

@Composable
fun PacienteVistaScreen(
    firstName: String,
    lastName: String,
    modifier: Modifier = Modifier,
    onUrgenciasClick: () -> Unit,
    onCloseSessionClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Muestra el nombre y apellido del usuario
        Text(
            text = "Bienvenido $firstName $lastName",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Botón para ingresar a urgencias
        Button(
            onClick = onUrgenciasClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Ingreso a Urgencias")
        }

        // Botón para historial de notificaciones (sin funcionalidad)
        Button(
            onClick = { /* Implementar funcionalidad si es necesario */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Historial de Notificaciones")
        }

        // Botón de cerrar sesión
        Button(
            onClick = onCloseSessionClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Cerrar sesión")
        }
    }
}

@Composable
fun PacienteVistaPreview() {
    AppTheme {
        PacienteVistaScreen(
            firstName = "Juan",
            lastName = "Pérez",
            onUrgenciasClick = {},
            onCloseSessionClick = {}
        )
    }
}
