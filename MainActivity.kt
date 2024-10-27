package com.example.notisalud_main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.notisalud_main.ui.theme.NotiSaludmainTheme
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.livedata.observeAsState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa la base de datos y el ViewModel
        val usuariosDao = UsuariosDatabase.getDatabase(application).usuariosDao()
        val repository = UsuariosRepository(usuariosDao)
        val viewModel: UsuariosViewModel by viewModels {
            UsuariosViewModelFactory(repository)
        }

        setContent {
            NotiSaludmainTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    LoginScreen(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth(),
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    viewModel: UsuariosViewModel
) {
    var run by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isLogin by remember { mutableStateOf(true) }
    var tipoDeUsuario by remember { mutableStateOf("Selecciona un tipo de usuario") }
    val tiposUsuarios = listOf("Paciente", "Enfermera", "Médico", "Paramédico", "Farmacia")
    val registroExitoso by viewModel.registroExitoso.observeAsState(false)
    val background = painterResource(id = R.drawable.fondo)
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = background,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isLogin) "Iniciar Sesión" else "Registro",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(20.dp))

            TextField(
                value = run,
                onValueChange = { run = it },
                label = { Text("RUN") },
                modifier = Modifier.fillMaxWidth()
            )

            if (!isLogin) {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Spinner para seleccionar el tipo de usuario
                AndroidView(
                    factory = { context ->
                        Spinner(context).apply {
                            adapter = ArrayAdapter(
                                context,
                                android.R.layout.simple_spinner_dropdown_item,
                                tiposUsuarios
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    update = { spinner ->
                        spinner.setSelection(tiposUsuarios.indexOf(tipoDeUsuario))
                        spinner.onItemSelectedListener =
                            object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>,
                                    view: View,
                                    position: Int,
                                    id: Long
                                ) {
                                    tipoDeUsuario = tiposUsuarios[position]
                                }

                                override fun onNothingSelected(parent: AdapterView<*>) {
                                    // Aquí puedes manejar el caso en que no se seleccione nada
                                }
                            }
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.autenticarUsuario(run, password)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLogin) "Acceder" else "Registrarse")
            }

// Observa autenticación
            val usuario = viewModel.autenticadoUsuario.observeAsState().value

            if (usuario != null) {
                // Verificar el tipo de usuario y redirigir a la actividad correspondiente
                if (usuario.tipo == "Paciente") {
                    val intent = Intent(context, PacienteActivity::class.java)
                    context.startActivity(intent)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { isLogin = !isLogin },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isLogin) "Registrar" else "Ya estoy registrado")
            }

            Spacer(modifier = Modifier.height(32.dp))
            LogoWrapper()
        }

        // Muestra un mensaje de éxito al registrar
        if (registroExitoso) {
            Text(
                text = "Registro exitoso",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
            )
        }
    }
}

@Composable
fun LogoWrapper(modifier: Modifier = Modifier) {
    val image = painterResource(id = R.drawable.logo)
    Image(
        painter = image,
        contentDescription = "Logo NotiSalud",
        modifier = modifier.size(200.dp)
    )
}

@Preview(showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    NotiSaludmainTheme {
        val dummyViewModel = UsuariosViewModel(UsuariosRepository(UsuariosDatabase.getDatabase(context = LocalContext.current).usuariosDao()))
        LoginScreen(viewModel = dummyViewModel)
    }
}
