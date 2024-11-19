package com.example.notisalud

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

class DoctorActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    DoctorActivity2Screen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
fun DoctorActivity2Screen(modifier: Modifier = Modifier) {
    val pacientesConExamenes = listOf(
        "Luis Martínez - Examen completado",
        "María Fernández - Examen completado"
    )

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Pacientes con exámenes realizados:", modifier = Modifier.padding(bottom = 16.dp))

        pacientesConExamenes.forEach { paciente ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = paciente, modifier = Modifier.weight(1f))

                Button(onClick = { /* Notificar funcionalidad */ }) {
                    Text("Notificar")
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun DoctorActivity2Preview() {
    AppTheme {
        DoctorActivity2Screen()
    }
}
