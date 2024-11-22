package com.example.notisalud.Paciente

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.MainActivity
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.platform.LocalContext

// Función para enviar los datos a Firestore
private fun enviarDatos(
    contexto: Context,
    problemaSalud: String,
    tieneFiebre: Boolean,
    fiebreDuracion: String,
    alergicoAlgo: Boolean,
    detallesAlergia: String,
    onSuccess: () -> Unit
) {
    val datos = hashMapOf(
        "nombre" to "Paciente Anónimo",
        "problemaSalud" to problemaSalud,
        "tieneFiebre" to tieneFiebre,
        "fiebreDuracion" to fiebreDuracion,
        "alergicoAlgo" to alergicoAlgo,
        "detallesAlergia" to detallesAlergia,
        "estado" to "pendiente" // Indica que el enfermero aún no valida este caso
    )

    val db = FirebaseFirestore.getInstance()

    db.collection("pacientes")
        .add(datos)
        .addOnSuccessListener {
            Toast.makeText(contexto, "Datos enviados correctamente", Toast.LENGTH_SHORT).show()
            onSuccess() // Llamar a la función onSuccess para cerrar la actividad
        }
        .addOnFailureListener { e ->
            Toast.makeText(contexto, "Error al enviar datos: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

class PacienteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener el nombre y apellido del intent
        val firstName = intent.getStringExtra("firstName") ?: ""
        val lastName = intent.getStringExtra("lastName") ?: ""

        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    PacienteScreen(
                        firstName = firstName,
                        lastName = lastName,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth(),
                        onLogout = { logoutUser() }
                    )
                }
            }
        }
    }

    private fun logoutUser() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

// Función para incluir el nombre del paciente
private fun enviarDatos(
    contexto: Context,
    firstName: String,
    lastName: String,
    problemaSalud: String,
    tieneFiebre: Boolean,
    fiebreDuracion: String,
    alergicoAlgo: Boolean,
    detallesAlergia: String,
    onSuccess: () -> Unit
) {
    val nombreCompleto = "$firstName $lastName"

    val datos = hashMapOf(
        "nombre" to nombreCompleto,
        "problemaSalud" to problemaSalud,
        "tieneFiebre" to tieneFiebre,
        "fiebreDuracion" to fiebreDuracion,
        "alergicoAlgo" to alergicoAlgo,
        "detallesAlergia" to detallesAlergia,
        "estado" to "pendiente"
    )

    val db = FirebaseFirestore.getInstance()

    db.collection("pacientes")
        .add(datos)
        .addOnSuccessListener {
            Toast.makeText(contexto, "Datos enviados correctamente", Toast.LENGTH_SHORT).show()
            onSuccess()
        }
        .addOnFailureListener { e ->
            Toast.makeText(contexto, "Error al enviar datos: ${e.message}", Toast.LENGTH_SHORT).show()
        }
}

@Composable
fun PacienteScreen(
    firstName: String,
    lastName: String,
    modifier: Modifier = Modifier,
    onLogout: () -> Unit
) {
    val contexto = LocalContext.current
    var problemaSalud by remember { mutableStateOf("") }
    var tieneFiebre by remember { mutableStateOf(false) }
    var fiebreDuracion by remember { mutableStateOf("") }
    var alergicoAlgo by remember { mutableStateOf(false) }
    var detallesAlergia by remember { mutableStateOf("") }

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Ingresar Problema de Salud",
            modifier = Modifier.padding(bottom = 16.dp),
        )

        TextField(
            value = problemaSalud,
            onValueChange = { problemaSalud = it },
            label = { Text("Describe tu problema de salud") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Check para fiebre
        Text("¿Tiene fiebre?")
        Checkbox(
            checked = tieneFiebre,
            onCheckedChange = { tieneFiebre = it }
        )

        if (tieneFiebre) {
            TextField(
                value = fiebreDuracion,
                onValueChange = { fiebreDuracion = it },
                label = { Text("¿Cuánto tiempo lleva con fiebre? (en días)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        Text("¿Es alérgico a algo?")
        Checkbox(
            checked = alergicoAlgo,
            onCheckedChange = { alergicoAlgo = it }
        )

        if (alergicoAlgo) {
            TextField(
                value = detallesAlergia,
                onValueChange = { detallesAlergia = it },
                label = { Text(text = "Describa la alergia") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = {
                if (problemaSalud.isBlank()) {
                    Toast.makeText(contexto, "Por favor, describe el problema de salud", Toast.LENGTH_SHORT).show()
                } else {
                    enviarDatos(
                        contexto,
                        firstName,
                        lastName,
                        problemaSalud,
                        tieneFiebre,
                        fiebreDuracion,
                        alergicoAlgo,
                        detallesAlergia
                    ) {
                        val intent = Intent(contexto, PacienteVista::class.java).apply {
                            putExtra("firstName", firstName)
                            putExtra("lastName", lastName)
                        }
                        contexto.startActivity(intent)
                        (contexto as? ComponentActivity)?.finish()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Enviar")
        }

        Button(
            onClick = {
                val intent = Intent(contexto, PacienteVista::class.java).apply {
                    putExtra("firstName", firstName)
                    putExtra("lastName", lastName)
                }
                contexto.startActivity(intent)
                (contexto as? ComponentActivity)?.finish()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}


