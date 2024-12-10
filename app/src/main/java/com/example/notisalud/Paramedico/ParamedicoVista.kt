package com.example.notisalud.Paramedico

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.MainActivity
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.auth.FirebaseAuth

class ParamedicoVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                ParamedicoVistaScreen(
                    onExamenesClick = {
                        // Redirige al usuario a la pantalla `ParamedicoActivity`
                        val intent = Intent(this, ParamedicoActivity::class.java)
                        startActivity(intent)
                    },
                    onCerrarSesionClick = {
                        cerrarSesion()
                    }
                )
            }
        }
    }

    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        Toast.makeText(this, "Sesión cerrada.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParamedicoVistaScreen(
    onExamenesClick: () -> Unit,
    onCerrarSesionClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Paramédico") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onExamenesClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Exámenes de Pacientes")
            }

            Button(
                onClick = onCerrarSesionClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Cerrar Sesión")
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PreviewParamedicoVistaScreen() {
    AppTheme {
        ParamedicoVistaScreen(
            onExamenesClick = { },
            onCerrarSesionClick = { }
        )
    }
}
