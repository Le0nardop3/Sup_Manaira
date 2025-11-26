package com.manaira.supmanaira.ui.registros

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.manaira.supmanaira.navigation.AppRoute

@Composable
fun RegistrosScreen(navController: NavHostController, context: Context = navController.context) {

    val viewModel: RegistroViewModel = viewModel(
        factory = RegistroViewModelFactory(context)
    )

    val registros = viewModel.registros.collectAsState()

    var abrirDialogCriar by remember { mutableStateOf(false) }
    var abrirDialogEditar by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { abrirDialogCriar = true }) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            Text(
                text = "Registros",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(registros.value) { registro ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            navController.navigate(AppRoute.Itens.createRoute(registro.id))
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = registro.nome, style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = "ID: ${registro.id}",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }

                            IconButton(onClick = { abrirDialogEditar = registro.id }) {
                                Icon(Icons.Default.Edit, contentDescription = "Editar")
                            }

                            IconButton(onClick = { viewModel.deletarRegistro(registro.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Excluir")
                            }

                            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "")
                        }
                    }
                }
            }
        }
    }

    if (abrirDialogCriar) {
        DialogCriarRegistro(
            onCancelar = { abrirDialogCriar = false },
            onConfirmar = { nome ->
                viewModel.criarRegistro(nome)
                abrirDialogCriar = false
            }
        )
    }

    abrirDialogEditar?.let { id ->
        DialogEditarRegistro(
            registroId = id,
            viewModel = viewModel,
            onFechar = { abrirDialogEditar = null }
        )
    }
}

@Composable
fun DialogCriarRegistro(onCancelar: () -> Unit, onConfirmar: (String) -> Unit) {

    var nome by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Criar novo registro") },
        text = {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome do registro") }
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (nome.isNotBlank()) onConfirmar(nome)
            }) {
                Text("Criar")
            }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun DialogEditarRegistro(
    registroId: Int,
    viewModel: RegistroViewModel,
    onFechar: () -> Unit
) {
    val registroAtual = viewModel.registros.value.find { it.id == registroId }

    var nome by remember { mutableStateOf(registroAtual?.nome ?: "") }

    AlertDialog(
        onDismissRequest = onFechar,
        title = { Text("Renomear registro") },
        text = {
            OutlinedTextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Novo nome") }
            )
        },
        confirmButton = {
            TextButton(onClick = {
                if (nome.isNotBlank()) {
                    viewModel.renomearRegistro(registroId, nome)
                    onFechar()
                }
            }) {
                Text("Salvar")
            }
        },
        dismissButton = {
            TextButton(onClick = onFechar) {
                Text("Cancelar")
            }
        }
    )
}
