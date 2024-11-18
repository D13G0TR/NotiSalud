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

class DoctorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    DoctorActivityScreen(
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
fun DoctorActivityScreen(modifier: Modifier = Modifier) {
    val pacientes = listOf(
        "Juan Pérez - Grave",
        "Ana López - Mediana gravedad",
        "Carlos Ramírez - Leve"
    )

    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text("Pacientes Categorizados:", modifier = Modifier.padding(bottom = 16.dp))

        pacientes.forEach { paciente ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = paciente, modifier = Modifier.weight(1f))

                Row(horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = { /* Dar de alta funcionalidad */ },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Dar de alta")
                    }

                    Button(onClick = { /* Petición de examen funcionalidad */ }) {
                        Text("Petición de examen")
                    }
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun DoctorActivityPreview() {
    AppTheme {
        DoctorActivityScreen()
    }
}
