package com.example.notisalud

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.notisalud.Enfermero.EnfermeroVista
import com.example.notisalud.Paciente.PacienteActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "Nuevo mensaje"
        val body = remoteMessage.notification?.body ?: "Tienes una nueva notificación"

        // Verifica si es una notificación de alta
        val type = remoteMessage.data["type"]
        val intent = if (type == "discharge") {
            val userId = remoteMessage.data["userId"]
            val problemaSalud = remoteMessage.data["problemaSalud"]

            Intent(this, PacienteActivity::class.java).apply {
                putExtra("userId", userId)
                putExtra("problemaSalud", problemaSalud)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
        } else {
            Intent(this, EnfermeroVista::class.java)
        }

        showNotification(title, body, intent)
    }

    private fun showNotification(title: String, body: String, intent: Intent) {
        val channelId = "default_channel"
        val notificationId = System.currentTimeMillis().toInt()

        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Crea el canal de notificaciones
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "General Notifications",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notificationId, builder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val db = FirebaseFirestore.getInstance()

        val userId = "enfermeroId"
        db.collection("Users")
            .document(userId)
            .update("fcmToken", token)
            .addOnSuccessListener {
                println("Token FCM guardado correctamente")
            }
            .addOnFailureListener { e ->
                println("Error al guardar el token FCM: ${e.message}")
            }
    }
}