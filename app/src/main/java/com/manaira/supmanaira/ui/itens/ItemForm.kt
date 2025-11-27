package com.manaira.supmanaira.ui.itens

import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Calendar

@Composable
fun DialogCriarItem(
    context: Context,
    onCancelar: () -> Unit,
    onSalvar: (codigo: String, nome: String, quantidade: Int, tipo: String, validade: String?, obs: String?) -> Unit,
    buscarDescricao: suspend (String) -> String?
) {
    var codigo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var validade by remember { mutableStateOf("") }
    var observacao by remember { mutableStateOf("") }

    val tipos = listOf("UNIDADE", "CAIXA", "FARDO", "PACOTE", "PALLET", "OUTRO")

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Adicionar Item") },
        text = {

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // ---- CÓDIGO + BOTÃO SCANNER ----
                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = { Text("Código do Produto") },
                    trailingIcon = {
                        IconButton(onClick = {
                            // ABRIR CÂMERA PARA LER CÓDIGO (IMPLEMENTAREMOS DEPOIS)
                        }) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = "Scanner")

                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // AUTO PREENCHIMENTO DA DESCRIÇÃO
                LaunchedEffect(codigo) {
                    if (codigo.length >= 5) {
                        val desc = buscarDescricao(codigo)
                        if (desc != null) descricao = desc
                    }
                }

                OutlinedTextField(
                    value = descricao,
                    onValueChange = { descricao = it },
                    label = { Text("Descrição do Produto") },
                    modifier = Modifier.fillMaxWidth()
                )

                // ---- QUANTIDADE ----
                OutlinedTextField(
                    value = quantidade,
                    onValueChange = { quantidade = it },
                    label = { Text("Quantidade") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // ---- DROPDOWN DE TIPOS ----
                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = tipo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        tipos.forEach {
                            DropdownMenuItem(
                                text = { Text(it) },
                                onClick = {
                                    tipo = it
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // ---- VALIDADE (DATE PICKER) ----
                OutlinedTextField(
                    value = validade,
                    onValueChange = {},
                    label = { Text("Validade") },
                    trailingIcon = {
                        IconButton(onClick = {
                            val calendar = Calendar.getInstance()
                            DatePickerDialog(
                                context,
                                { _, year, month, day ->
                                    validade = "%02d/%02d/%04d".format(day, month + 1, year)
                                },
                                calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH),
                                calendar.get(Calendar.DAY_OF_MONTH)
                            ).show()
                        }) {
                            Icon(Icons.Filled.DateRange, contentDescription = "Selecionar validade")
                        }
                    },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // ---- OBSERVAÇÃO ----
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
                if (codigo.isNotBlank() && quantidade.isNotBlank() && tipo.isNotBlank()) {
                    onSalvar(
                        codigo,
                        descricao,
                        quantidade.toInt(),
                        tipo,
                        validade.ifBlank { null },
                        observacao.ifBlank { null }
                    )
                }
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
