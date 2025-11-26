package com.manaira.supmanaira.ui.itens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.manaira.supmanaira.ui.itens.ItemViewModel
import com.manaira.supmanaira.ui.itens.ItemViewModelFactory
import com.manaira.supmanaira.data.local.entities.ItemEntity

@Composable
fun ItensScreen(
    navController: NavHostController,
    registroId: Int,
    context: Context = navController.context
) {
    val viewModel: ItemViewModel = viewModel(
        factory = ItemViewModelFactory(registroId, context)
    )

    val itens = viewModel.itens.collectAsState()

    var abrirDialogCriar by remember { mutableStateOf(false) }
    var itemParaEditar by remember { mutableStateOf<ItemEntity?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { abrirDialogCriar = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar item")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = "Itens do Registro", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(itens.value) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(item.nome, style = MaterialTheme.typography.titleMedium)
                            Text("Qtd: ${item.quantidade}")
                            Text("Tipo: ${item.tipo}")
                            Text("Validade: ${item.validade ?: "—"}")

                            Row {
                                IconButton(onClick = { itemParaEditar = item }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }
                                IconButton(onClick = { viewModel.deletarItem(item) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Excluir")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (abrirDialogCriar) {
        DialogCriarItem(
            onCancelar = { abrirDialogCriar = false },
            onConfirmar = { nome, quantidade, tipo, validade ->
                viewModel.criarItem(
                    nome = nome,
                    quantidade = quantidade,
                    tipo = tipo,
                    validade = validade
                )
                abrirDialogCriar = false
            }
        )
    }

    itemParaEditar?.let { item ->
        DialogEditarItem(
            itemAtual = item,
            onCancelar = { itemParaEditar = null },
            onConfirmar = { nome, quantidade, tipo, validade ->
                viewModel.atualizarItem(
                    item.copy(
                        nome = nome,
                        quantidade = quantidade,
                        tipo = tipo,
                        validade = validade
                    )
                )
                itemParaEditar = null
            }
        )
    }
}


/* ------------------------------------------------------------------ */
/*                          DIALOG CRIAR ITEM                         */
/* ------------------------------------------------------------------ */

@Composable
fun DialogCriarItem(
    onCancelar: () -> Unit,
    onConfirmar: (String, Int, String, String?) -> Unit
) {
    var nome by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("0") }
    var tipo by remember { mutableStateOf("") }
    var validade by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Adicionar item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") }
                )

                OutlinedTextField(
                    value = quantidade,
                    onValueChange = { quantidade = it },
                    label = { Text("Quantidade") }
                )

                OutlinedTextField(
                    value = tipo,
                    onValueChange = { tipo = it },
                    label = { Text("Tipo") }
                )

                OutlinedTextField(
                    value = validade,
                    onValueChange = { validade = it },
                    label = { Text("Validade (opcional)") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (nome.isNotBlank()) {
                    onConfirmar(
                        nome,
                        quantidade.toIntOrNull() ?: 0,
                        tipo,
                        validade.ifBlank { null }
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

/* ------------------------------------------------------------------ */
/*                          DIALOG EDITAR ITEM                        */
/* ------------------------------------------------------------------ */

@Composable
fun DialogEditarItem(
    itemAtual: ItemEntity,
    onCancelar: () -> Unit,
    onConfirmar: (String, Int, String, String?) -> Unit
) {
    var nome by remember { mutableStateOf(itemAtual.nome) }
    var quantidade by remember { mutableStateOf(itemAtual.quantidade.toString()) }
    var tipo by remember { mutableStateOf(itemAtual.tipo) }
    var validade by remember { mutableStateOf(itemAtual.validade ?: "") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Editar item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") }
                )

                OutlinedTextField(
                    value = quantidade,
                    onValueChange = { quantidade = it },
                    label = { Text("Quantidade") }
                )

                OutlinedTextField(
                    value = tipo,
                    onValueChange = { tipo = it },
                    label = { Text("Tipo") }
                )

                OutlinedTextField(
                    value = validade,
                    onValueChange = { validade = it },
                    label = { Text("Validade") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirmar(
                    nome,
                    quantidade.toIntOrNull() ?: 0,
                    tipo,
                    validade.ifBlank { null }
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
