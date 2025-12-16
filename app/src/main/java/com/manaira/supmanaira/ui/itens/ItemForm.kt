package com.manaira.supmanaira.ui.itens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.manaira.supmanaira.navigation.AppRoute
import kotlinx.coroutines.delay
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemForm(
    navController: NavHostController,
    registroId: Int,
    onCancelar: () -> Unit,
    onSalvar: (
        codigo: String,
        descricao: String,
        quantidade: String,
        validade: String?,
        obs: String?
    ) -> Unit,
    buscarDescricao: suspend (String) -> String?
) {
    val context = LocalContext.current

    var codigo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    var validade by remember { mutableStateOf("") }
    var observacao by remember { mutableStateOf("") }

    /* ---------------------------------------------------------
       LISTENER — SCANNER (EVENTO DE UMA VEZ)
       --------------------------------------------------------- */
    val scannedFlow = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("scanned_code", null)

    LaunchedEffect(scannedFlow) {
        scannedFlow?.collect { code ->
            if (!code.isNullOrBlank() && code != codigo) {

                codigo = code

                // consome o evento imediatamente
                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("scanned_code", null)

            }
        }
    }

    /* ---------------------------------------------------------
       DESCRIÇÃO AUTOMÁTICA (COM DEBOUNCE)
       --------------------------------------------------------- */
    LaunchedEffect(codigo) {
        if (codigo.length >= 1) {
            delay(700)
            val desc = buscarDescricao(codigo)
            if (!desc.isNullOrBlank()) {
                descricao = desc
            }
        }
    }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Adicionar Item") },

        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = codigo,
                    onValueChange = {
                        codigo = it
                        descricao = "" // força nova busca se digitar manualmente
                    },
                    label = { Text("Código (scanner ou manual)") },
                    trailingIcon = {
                        IconButton(onClick = {

                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("scanned_code", null)

                            navController.navigate(
                                AppRoute.Scanner.createRoute(registroId)
                            )
                        }) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = "Scanner")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = descricao,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = { Text("Descrição do Produto") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = quantidade,
                    onValueChange = { quantidade = it },
                    label = { Text("Quantidade") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = validade,
                    readOnly = true,
                    onValueChange = {},
                    label = { Text("Validade") },
                    trailingIcon = {
                        IconButton(onClick = {
                            val cal = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, y, m, d ->
                                    validade = "%02d/%02d/%04d".format(d, m + 1, y)
                                },
                                cal.get(Calendar.YEAR),
                                cal.get(Calendar.MONTH),
                                cal.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }) {
                            Icon(Icons.Filled.CalendarToday, "Selecionar data")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = observacao,
                    onValueChange = { observacao = it },
                    label = { Text("Observação (opcional)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {
            TextButton(onClick = {
                onSalvar(
                    codigo.trim(),
                    descricao.trim(),
                    quantidade.trim(),
                    validade.ifBlank { null },
                    observacao.ifBlank { null }
                )
            }) {
                Text("Salvar")
            }
        },

        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}
