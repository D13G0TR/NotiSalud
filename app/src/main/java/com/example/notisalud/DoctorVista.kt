// DoctorVista.kt
package com.example.notisalud

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.notisalud.ui.theme.AppTheme

class DoctorVista : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    DoctorVistaScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth(),
                        onPacientesClick = {
                            val intent = Intent(this, DoctorActivity::class.java)
                            startActivity(intent)
                        },
                        onExamenesClick = {
                            val intent = Intent(this, DoctorActivity2::class.java)
                            startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DoctorVistaScreen(
    modifier: Modifier = Modifier,
    onPacientesClick: () -> Unit,
    onExamenesClick: () -> Unit
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Panel del Doctor", modifier = Modifier.padding(bottom = 16.dp))

        Button(
            onClick = onPacientesClick,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text("Pacientes Categorizados")
        }

        Button(
            onClick = onExamenesClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Exámenes de Paciente")
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun DoctorVistaPreview() {
    AppTheme {
        DoctorVistaScreen(
            onPacientesClick = {},
            onExamenesClick = {}
        )
    }
}