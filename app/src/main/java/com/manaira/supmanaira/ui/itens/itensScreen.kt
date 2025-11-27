package com.manaira.supmanaira.ui.itens

import android.app.DatePickerDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.manaira.supmanaira.data.local.entities.ItemEntity
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
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
            Text(
                text = "Itens do Registro",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(itens.value) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            if (!item.codigo.isNullOrBlank()) {
                                Text("Código: ${item.codigo}", style = MaterialTheme.typography.bodyMedium)
                            }
                            Text(item.nome, style = MaterialTheme.typography.titleMedium)
                            Text("Qtd: ${item.quantidade}")
                            Text("Tipo: ${item.tipo}")
                            Text("Validade: ${item.validade ?: "—"}")
                            if (!item.observacao.isNullOrBlank()) {
                                Text("Obs: ${item.observacao}")
                            }

                            Spacer(Modifier.height(8.dp))

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
            onBuscarDescricao = { codigo ->
                viewModel.buscarDescricaoPorCodigo(codigo)
            },
            onConfirmar = { codigo, nome, quantidade, tipo, validade, observacao ->
                viewModel.criarItem(
                    codigo = codigo.ifBlank { null },
                    nome = nome,
                    quantidade = quantidade,
                    tipo = tipo,
                    validade = validade,
                    observacao = observacao.ifBlank { null }
                )
                abrirDialogCriar = false
            }
        )
    }

    itemParaEditar?.let { item ->
        DialogEditarItem(
            itemAtual = item,
            onCancelar = { itemParaEditar = null },
            onConfirmar = { codigo, nome, quantidade, tipo, validade, observacao ->
                viewModel.atualizarItem(
                    item.copy(
                        codigo = codigo.ifBlank { null },
                        nome = nome,
                        quantidade = quantidade,
                        tipo = tipo,
                        validade = validade,
                        observacao = observacao.ifBlank { null }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogCriarItem(
    onCancelar: () -> Unit,
    onBuscarDescricao: suspend (String) -> String?,
    onConfirmar: (String, String, Int, String, String?, String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var codigo by remember { mutableStateOf("") }
    var nome by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("0") }
    var tipo by remember { mutableStateOf("") }
    var validade by remember { mutableStateOf("") }
    var observacao by remember { mutableStateOf("") }

    val tipos = listOf("UN", "CX", "FD", "KG", "PCT")
    var tipoExpanded by remember { mutableStateOf(false) }

    fun abrirDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                validade = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Adicionar item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = { Text("Código (leitor de barras)") },
                    singleLine = true
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val cod = codigo.trim()
                            if (cod.isNotEmpty()) {
                                scope.launch {
                                    val desc = onBuscarDescricao(cod)
                                    if (!desc.isNullOrBlank()) {
                                        nome = desc
                                    }
                                }
                            }
                        }
                    ) {
                        Text("Buscar descrição")
                    }

                    // Aqui no futuro você pode colocar um botão
                    // pra abrir o scanner de câmera (MLKit)
                    // Button(onClick = { /* TODO: abrir tela de scanner */ }) {
                    //     Text("Ler com câmera")
                    // }
                }

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Descrição / Nome") }
                )

                OutlinedTextField(
                    value = quantidade,
                    onValueChange = { quantidade = it },
                    label = { Text("Quantidade") },
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = tipoExpanded,
                    onExpandedChange = { tipoExpanded = it }
                ) {
                    OutlinedTextField(
                        value = tipo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = tipoExpanded,
                        onDismissRequest = { tipoExpanded = false }
                    ) {
                        tipos.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = {
                                    tipo = t
                                    tipoExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = validade,
                        onValueChange = {},
                        label = { Text("Validade") },
                        readOnly = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { abrirDatePicker() }) {
                        Text("Selecionar data")
                    }
                }

                OutlinedTextField(
                    value = observacao,
                    onValueChange = { observacao = it },
                    label = { Text("Observação") },
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (nome.isNotBlank()) {
                    onConfirmar(
                        codigo,
                        nome,
                        quantidade.toIntOrNull() ?: 0,
                        tipo,
                        validade.ifBlank { null },
                        observacao
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogEditarItem(
    itemAtual: ItemEntity,
    onCancelar: () -> Unit,
    onConfirmar: (String, String, Int, String, String?, String) -> Unit
) {
    val context = LocalContext.current

    var codigo by remember { mutableStateOf(itemAtual.codigo ?: "") }
    var nome by remember { mutableStateOf(itemAtual.nome) }
    var quantidade by remember { mutableStateOf(itemAtual.quantidade.toString()) }
    var tipo by remember { mutableStateOf(itemAtual.tipo) }
    var validade by remember { mutableStateOf(itemAtual.validade ?: "") }
    var observacao by remember { mutableStateOf(itemAtual.observacao ?: "") }

    val tipos = listOf("UN", "CX", "FD", "KG", "PCT")
    var tipoExpanded by remember { mutableStateOf(false) }

    fun abrirDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                validade = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Editar item") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                OutlinedTextField(
                    value = codigo,
                    onValueChange = { codigo = it },
                    label = { Text("Código") },
                    singleLine = true
                )

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Descrição / Nome") }
                )

                OutlinedTextField(
                    value = quantidade,
                    onValueChange = { quantidade = it },
                    label = { Text("Quantidade") },
                    singleLine = true
                )

                ExposedDropdownMenuBox(
                    expanded = tipoExpanded,
                    onExpandedChange = { tipoExpanded = it }
                ) {
                    OutlinedTextField(
                        value = tipo,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tipo") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded) },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = tipoExpanded,
                        onDismissRequest = { tipoExpanded = false }
                    ) {
                        tipos.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = {
                                    tipo = t
                                    tipoExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = validade,
                        onValueChange = {},
                        label = { Text("Validade") },
                        readOnly = true,
                        modifier = Modifier.weight(1f)
                    )
                    Button(onClick = { abrirDatePicker() }) {
                        Text("Selecionar data")
                    }
                }

                OutlinedTextField(
                    value = observacao,
                    onValueChange = { observacao = it },
                    label = { Text("Observação") },
                    minLines = 2
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onConfirmar(
                    codigo,
                    nome,
                    quantidade.toIntOrNull() ?: 0,
                    tipo,
                    validade.ifBlank { null },
                    observacao
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
