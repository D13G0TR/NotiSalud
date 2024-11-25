package com.example.notisalud.Medico

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PacienteUrgencia(
    val id: String = "",
    val nombre: String = "",
    val urgencia: String = "",
    val problemaSalud: String = "",
    val fiebre: String = "",
    val alergia: String = "",
    val validado: Boolean = false
) : Parcelable