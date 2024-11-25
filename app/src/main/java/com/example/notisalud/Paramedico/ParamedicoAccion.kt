package com.example.notisalud.Paramedico

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notisalud.ui.theme.AppTheme
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.launch

class ParamedicoAccion : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pacienteJson = intent.getStringExtra("paciente")
        val paciente = Gson().fromJson(pacienteJson, PacienteAccion::class.java)

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ParamedicoAccionScreen(
                        paciente = paciente,
                        onBack = { finish() }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParamedicoAccionScreen(
    paciente: PacienteAccion,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Estados para los checkboxes
    var atendido by remember { mutableStateOf(paciente.atendido) }
    var examenRealizado by remember { mutableStateOf(paciente.examenRealizado) }
    var informeListo by remember { mutableStateOf(paciente.informeListo) }

    // Estado para el diálogo de confirmación
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Estado para mostrar el progreso
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Atención al Paciente") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Información del paciente
            Card(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Datos del Paciente",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(text = "Nombre: ${paciente.nombres}")
                    Text(text = "Categoría: ${paciente.categorization}")
                    Text(text = "Atención: ${paciente.atencion}")
                    Text(text = "Examen solicitado: ${paciente.examenSolicitado}")
                }
            }

            // Acciones del paramédico
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Estado de la Atención",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    CustomCheckboxRow(
                        checked = atendido,
                        onCheckedChange = { atendido = it },
                        label = "Paciente atendido"
                    )

                    CustomCheckboxRow(
                        checked = examenRealizado,
                        onCheckedChange = { examenRealizado = it },
                        label = "Examen realizado",
                        enabled = atendido
                    )

                    CustomCheckboxRow(
                        checked = informeListo,
                        onCheckedChange = { informeListo = it },
                        label = "Informe listo",
                        enabled = atendido && examenRealizado
                    )
                }
            }

            // Botón de guardar
            Button(
                onClick = { showConfirmDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = !isLoading && (
                        atendido != paciente.atendido ||
                                examenRealizado != paciente.examenRealizado ||
                                informeListo != paciente.informeListo
                        )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text("Guardar cambios")
                }
            }
        }

        // Diálogo de confirmación
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                title = { Text("Confirmar cambios") },
                text = { Text("¿Está seguro que desea guardar los cambios realizados?") },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmDialog = false
                            isLoading = true
                            scope.launch {
                                try {
                                    guardarEstado(
                                        paciente.id,
                                        atendido,
                                        examenRealizado,
                                        informeListo,
                                        onSuccess = {
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Cambios guardados exitosamente",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onBack()
                                        },
                                        onError = { error ->
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Error: $error",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    )
                                } catch (e: Exception) {
                                    isLoading = false
                                    Toast.makeText(
                                        context,
                                        "Error inesperado: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                            }
                        }
                    ) {
                        Text("Confirmar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun CustomCheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp),
            color = if (enabled) MaterialTheme.colorScheme.onSurface
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        )
    }
}

private fun guardarEstado(
    pacienteId: String,
    atendido: Boolean,
    examenRealizado: Boolean,
    informeListo: Boolean,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val db = FirebaseFirestore.getInstance()
    val updates = mapOf(
        "atendido" to atendido,
        "examenRealizado" to examenRealizado,
        "informeListo" to informeListo
    )

    db.collection("pacientes")
        .document(pacienteId)
        .update(updates)
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { e ->
            onError(e.message ?: "Error desconocido")
        }
}