package com.example.notisalud.Examen

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notisalud.MainActivity
import com.example.notisalud.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
class LaboratorioVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("Laboratorio") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                                }
                            }
                        )
                    }
                ) { padding ->
                    LaboratorioVistaScreen(
                        modifier = Modifier.padding(padding),
                        onAnalisisClick = {
                            // Redirige al usuario a LaboratorioActivity
                            val intent = Intent(this, LaboratorioActivity::class.java)
                            startActivity(intent)
                        },
                        onCerrarSesionClick = {
                            cerrarSesion()
                        }
                    )
                }
            }
        }
    }

    private fun cerrarSesion() {
        Toast.makeText(this, "Sesión cerrada.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}

@Composable
fun LaboratorioVistaScreen(
    modifier: Modifier = Modifier,
    onAnalisisClick: () -> Unit,
    onCerrarSesionClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onAnalisisClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Análisis de Exámenes")
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