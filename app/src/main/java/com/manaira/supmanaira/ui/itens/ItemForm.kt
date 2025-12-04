package com.manaira.supmanaira.ui.itens

import android.app.DatePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Arrangement
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
    onSalvar: (codigo: String, descricao: String, quantidade: String, validade: String?, obs: String?) -> Unit,
    buscarDescricao: suspend (String) -> String?
) {
    val context = LocalContext.current

    var codigo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    var validade by remember { mutableStateOf("") }
    var observacao by remember { mutableStateOf("") }

    // ---------------------------------------------------------
    // ⭐ ESCUTA O MESMO HANDLE DA ItensScreen → FUNCIONA!
    // ---------------------------------------------------------
    val scannedFlow = navController
        .currentBackStackEntry
        ?.savedStateHandle
        ?.getStateFlow<String?>("scanned_code", null)

    LaunchedEffect(scannedFlow) {
        scannedFlow?.collect { code ->
            Log.d("DEBUG_FORM", "ItemForm recebeu scanned_code = $code")

            if (!code.isNullOrBlank()) {
                codigo = code
            }
        }
    }

    // ---------------------------------------------------------
    // DESCRIÇÃO AUTOMÁTICA
    // ---------------------------------------------------------
    LaunchedEffect(codigo) {
        if (codigo.length >= 5) {
            delay(120)
            val desc = buscarDescricao(codigo)
            if (!desc.isNullOrBlank()) descricao = desc
        }
    }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Adicionar Item") },

        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = { Text("Código (via scanner)") },
                    trailingIcon = {
                        IconButton(onClick = {

                            // limpa antes de abrir o scanner
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
                    onValueChange = { descricao = it },
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
                    onValueChange = {},
                    readOnly = true,
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
                    codigo,
                    descricao,
                    quantidade, // agora String
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
