package com.example.notisalud.Medico

import android.content.Intent
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth

class MedicoVista : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()

        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { padding ->
                    MedicoVistaScreen(
                        modifier = Modifier.padding(padding),
                        onPacientesCategorizadosClick = {
                            // Redirigir a MedicoPacienteActivity
                            val intent = Intent(this, MedicoPacienteActivity::class.java)
                            startActivity(intent)
                        },
                        onCerrarSesionClick = {
                            // Cerrar sesión y redirigir a MainActivity
                            auth.signOut()
                            val intent = Intent(this, com.example.notisalud.MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MedicoVistaScreen(
    modifier: Modifier = Modifier,
    onPacientesCategorizadosClick: () -> Unit,
    onCerrarSesionClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Botón de Pacientes Categorizados
        Button(
            onClick = onPacientesCategorizadosClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Pacientes Categorizados")
        }

        // Botón de Cerrar Sesión
        Button(
            onClick = onCerrarSesionClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Text(text = "Cerrar Sesión")
        }

        // Botón de Validación de Laboratorio (sin funcionalidad por ahora)
        Button(
            onClick = { /* TODO: Implementar funcionalidad futura */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Validación de Laboratorio")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun MedicoVistaPreview() {
    AppTheme {
        MedicoVistaScreen(
            onPacientesCategorizadosClick = {},
            onCerrarSesionClick = {}
        )
    }
}
