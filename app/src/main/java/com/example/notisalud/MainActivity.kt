package com.example.notisalud

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.notisalud.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    LoginScreen(
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
fun LoginScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            color = MaterialTheme.colorScheme.primary,
        ) {
            Text(
                text = "Bienvenido a NotiSalud",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(16.dp)
            )
        }

        Surface(
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = "Tu salud nos importa",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Campos de texto para Usuario y Contraseña
        var username by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Button(
            onClick = { /* Aquí irá la lógica para iniciar sesión */ },
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
        ) {
            Text("Iniciar sesión")
        }

        LogoWrapper(modifier = Modifier.padding(top = 32.dp))
    }
}

@Composable
fun LogoWrapper(modifier: Modifier = Modifier) {
    val image = painterResource(id = R.drawable.logo)
    Image(
        painter = image,
        contentDescription = "Logo NotiSalud",
        modifier = modifier
    )
}


@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    AppTheme {
        LoginScreen()
    }
}