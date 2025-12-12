package com.manaira.supmanaira.ui.itens

import android.util.Log
import android.app.DatePickerDialog
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.manaira.supmanaira.data.local.entities.ItemEntity
import com.manaira.supmanaira.navigation.AppRoute
import com.manaira.supmanaira.utils.ExportUtils
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar
import com.manaira.supmanaira.utils.ProdutoJsonUtils
import com.manaira.supmanaira.ui.registros.RegistroViewModel
import com.manaira.supmanaira.ui.registros.RegistroViewModelFactory

/* ================================================================
   TELA PRINCIPAL DOS ITENS
   ================================================================ */

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

    var abrirFormCriar by remember { mutableStateOf(false) }
    var itemParaEditar by remember { mutableStateOf<ItemEntity?>(null) }

    // ⚠️ TÍTULO DO RELATÓRIO
    val registroViewModel: RegistroViewModel = viewModel(
        factory = RegistroViewModelFactory(context)
    )
    val tituloPlanilha by registroViewModel.tituloRelatorio.collectAsState()

    var tituloEditavel by remember {
        mutableStateOf("")
    }

    /* -----------------------------------------
       LISTENER — SCANNER (CRIAR)
       ----------------------------------------- */
    val scannedCreateFlow: StateFlow<String?>? =
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("scanned_code", null)



    LaunchedEffect(scannedCreateFlow) {
        scannedCreateFlow?.collectLatest { code ->
            if (!code.isNullOrBlank() && !abrirFormCriar) {
                abrirFormCriar = true
            }
        }
    }
    LaunchedEffect(registroId) {
        registroViewModel.carregarTitulo(registroId)
    }

    LaunchedEffect(tituloPlanilha) {
        if (tituloEditavel.isBlank()) {
            tituloEditavel = tituloPlanilha
        }
    }

    /* -----------------------------------------
       LISTENER — SCANNER (EDITAR)
       ----------------------------------------- */
    val scannedEditFlow: StateFlow<String?>? =
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("edit_scanned_code", null)

    LaunchedEffect(scannedEditFlow) {
        scannedEditFlow?.collectLatest { code ->
            if (!code.isNullOrBlank() && itemParaEditar != null) {
                itemParaEditar = itemParaEditar!!.copy(codigo = code)
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { abrirFormCriar = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar item")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            /* CAMPO — TÍTULO DA PLANILHA */
            OutlinedTextField(
                value = tituloEditavel,
                onValueChange = {
                    tituloEditavel = it
                    registroViewModel.atualizarTitulo(registroId, it)
                },
                label = { Text("Título da Planilha") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(10.dp))

            /* BOTÃO — EXPORTAR */
            Button(
                onClick = {

                    val nomeArquivo = tituloEditavel
                        .trim()
                        .ifBlank { "Registro_$registroId" }
                        // troca / por .
                        .replace("/", ".")
                        // troca espaços por _
                        .replace("\\s+".toRegex(), "_")
                        // mantém letras, números, _ e .
                        .replace("[^a-zA-Z0-9_.]".toRegex(), "")


                    val uri = ExportUtils.exportarExcel(
                        context = context,
                        titulo = nomeArquivo,
                        nomeRegistro = nomeArquivo,
                        itens = itens.value
                    )

                    if (uri != null) {
                        ExportUtils.compartilharArquivo(context, uri)
                    }

                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Upload, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Exportar XLSX")
            }

            Spacer(Modifier.height(20.dp))

            /* ----------------------------------------------- */

            Text("Itens do Registro", style = MaterialTheme.typography.headlineMedium)

            Spacer(Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(itens.value) { item ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {

                            item.codigo?.takeIf { it.isNotBlank() }?.let { codigo ->
                                Text("Código: $codigo")
                            }

                            Text(item.nome, style = MaterialTheme.typography.titleMedium)
                            Text("Quantidade: ${item.quantidade}")
                            Text("Validade: ${item.validade ?: "—"}")

                            if (!item.observacao.isNullOrBlank())
                                Text("Obs: ${item.observacao}")

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

    /* ============================================================
       FORMULÁRIO DE CRIAÇÃO
       ============================================================ */
    if (abrirFormCriar) {

        ItemForm(
            navController = navController,
            registroId = registroId,

            onCancelar = {

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("scanned_code", null)

                abrirFormCriar = false
            },

            onSalvar = { codigo, descricao, quantidade, validade, obs ->

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("scanned_code", null)

                viewModel.criarItem(
                    codigo = codigo,
                    nome = descricao,
                    quantidade = quantidade,
                    tipo = "",
                    validade = validade,
                    observacao = obs
                )

                abrirFormCriar = false
            },

            buscarDescricao = { codigo ->
                ProdutoJsonUtils.buscarDescricao(context, codigo)
            }
        )
    }

    /* ============================================================
       FORMULÁRIO DE EDIÇÃO
       ============================================================ */
    itemParaEditar?.let { item ->

        DialogEditarItem(
            navController = navController,
            registroId = registroId,
            itemAtual = item,

            onCancelar = { itemParaEditar = null },

            onConfirmar = { codigo, nome, quantidade, validade, observacao ->

                viewModel.atualizarItem(
                    item.copy(
                        codigo = codigo,
                        nome = nome,
                        quantidade = quantidade,
                        validade = validade,
                        observacao = observacao
                    )
                )

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("edit_scanned_code", null)

                itemParaEditar = null
            }
        )
    }
}


/* ================================================================
   DIALOG DE EDIÇÃO — COM SCANNER SEPARADO
   ================================================================ */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogEditarItem(
    navController: NavHostController,
    registroId: Int,
    itemAtual: ItemEntity,
    onCancelar: () -> Unit,
    onConfirmar: (String, String, String, String?, String?) -> Unit
) {
    val context = LocalContext.current

    var codigo by remember { mutableStateOf(itemAtual.codigo ?: "") }
    var nome by remember { mutableStateOf(itemAtual.nome) }
    var quantidade by remember { mutableStateOf(itemAtual.quantidade) }
    var validade by remember { mutableStateOf(itemAtual.validade ?: "") }
    var observacao by remember { mutableStateOf(itemAtual.observacao ?: "") }

    /* LISTENER — EDITAR */
    LaunchedEffect(Unit) {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getStateFlow("edit_scanned_code", null)
            ?.collect { code ->
                code?.takeIf { it.isNotBlank() }?.let { valor ->
                    codigo = valor
                }
            }
    }

    fun abrirDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                validade = "%02d/%02d/%04d".format(day, month + 1, year)
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
                    trailingIcon = {
                        IconButton(onClick = {

                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("edit_scanned_code", null)

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
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = quantidade,
                    onValueChange = { quantidade = it },
                    label = { Text("Quantidade (texto livre)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = validade,
                    readOnly = true,
                    onValueChange = {},
                    label = { Text("Validade") },
                    trailingIcon = {
                        IconButton(onClick = { abrirDatePicker() }) {
                            Icon(Icons.Filled.CalendarToday, contentDescription = "Selecionar data")
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = observacao,
                    onValueChange = { observacao = it },
                    label = { Text("Observação") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )
            }
        },

        confirmButton = {
            TextButton(onClick = {
                onConfirmar(
                    codigo,
                    nome,
                    quantidade,
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
