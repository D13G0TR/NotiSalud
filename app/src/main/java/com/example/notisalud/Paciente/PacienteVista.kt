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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PacienteVista : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa Firebase Auth y Firestore
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Crea canal de notificaciones
        createNotificationChannel()

        // Recupera el nombre y apellido del intent
        val firstName = intent.getStringExtra("firstName") ?: "Nombre no disponible"
        val lastName = intent.getStringExtra("lastName") ?: "Apellido no disponible"

        // Configura el listener para notificaciones en tiempo real
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
                        },
                        onHistorialClick = {
                            // Navegar a PacienteHistorial
                            val intent = Intent(this, PacienteHistorial::class.java).apply {
                                putExtra("firstName", firstName)
                                putExtra("lastName", lastName)
                            }
                            startActivity(intent)
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

                    // Verifica si hay un estado de examen "Notificando"
                    val estadoExamen = data["EstadodeExamen"] as? String
                    val examen = data["Examen"] as? String ?: "Solicitado"
                    if (estadoExamen == "Notificando") {
                        val titulo = "Tu examen: $examen está listo"
                        val mensaje = "Tu examen: $examen está listo. Por favor, acércate al mesón."

                        mostrarNotificacion(titulo, mensaje)
                        guardarNotificacion(titulo, mensaje)
                    }

                    // Verifica si hay alta médica
                    val motivoAlta = data["MotivoAlta"] as? String
                    val fechaAlta = data["FechaAlta"] as? String
                    if (motivoAlta != null && fechaAlta != null) {
                        val titulo = "Alta Médica"
                        val mensaje = "Has sido dado de alta el $fechaAlta. Motivo: $motivoAlta."

                        mostrarNotificacion(titulo, mensaje)
                        guardarNotificacion(titulo, mensaje)
                    }
                }
            }
    }

    private fun guardarNotificacion(titulo: String, mensaje: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val notificacion = hashMapOf(
                "titulo" to titulo,
                "mensaje" to mensaje,
                "fecha" to SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            )

            // Verifica si la notificación ya existe
            firestore.collection("Users")
                .document(userId)
                .collection("notificaciones")
                .whereEqualTo("titulo", titulo)
                .whereEqualTo("mensaje", mensaje)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        // Si no existe la guarda
                        firestore.collection("Users")
                            .document(userId)
                            .collection("notificaciones")
                            .add(notificacion)
                            .addOnSuccessListener {
                                println("Notificación guardada exitosamente")
                            }
                            .addOnFailureListener { exception ->
                                println("Error al guardar notificación: ${exception.message}")
                            }
                    } else {
                        println("La notificación ya existe y no se guardará de nuevo.")
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error al verificar notificación existente: ${exception.message}")
                }
        }
    }


    private fun mostrarNotificacion(titulo: String, mensaje: String) {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notificationId = (System.currentTimeMillis() % 10000).toInt()
        val notification = NotificationCompat.Builder(this, "examenChannel")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
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
                "Notificaciones de Examen y Alta",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones relacionadas con exámenes y alta médica."
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
    onCloseSessionClick: () -> Unit,
    onHistorialClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido $firstName $lastName",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = onUrgenciasClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Ingreso a Urgencias")
        }

        Button(
            onClick = onHistorialClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Historial de Notificaciones")
        }

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