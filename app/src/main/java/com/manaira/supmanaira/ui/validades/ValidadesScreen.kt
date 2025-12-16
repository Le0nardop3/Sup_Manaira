package com.manaira.supmanaira.ui.validades

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValidadesScreen(
    navController: NavHostController
) {
    val context = LocalContext.current

    val viewModel: ValidadesViewModel = viewModel(
        factory = ValidadesViewModelFactory(context)
    )

    val itens by viewModel.itensOrdenados.collectAsState()

    // 🔴 CONTROLE DO DIÁLOGO DE EXCLUSÃO
    var itemParaExcluir by remember { mutableStateOf<ItemValidadeUI?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Controle de Validades") }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(itens) { ui ->

                val backgroundColor = when {
                    ui.diasParaVencer < 0 -> Color(0xFFFFE0E0)
                    ui.diasParaVencer < 30 -> Color(0xFFFFEBEE)
                    ui.diasParaVencer < 60 -> Color(0xFFFFF3E0)
                    else -> Color(0xFFE8F5E9)
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = backgroundColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        // 📄 CONTEÚDO
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = ui.item.nome,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(Modifier.height(8.dp))

                            Text(
                                text = "Validade: ${ui.item.validade}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.DarkGray
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = "Dias para vencer: ${ui.diasParaVencer}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    ui.diasParaVencer < 0 -> Color(0xFFD32F2F)
                                    ui.diasParaVencer < 30 -> Color.Red
                                    ui.diasParaVencer < 60 -> Color(0xFFFF9800)
                                    else -> Color(0xFF2E7D32)
                                }
                            )

                            if (ui.diasParaVencer < 0) {
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = "⚠️ PRODUTO VENCIDO",
                                    color = Color(0xFFD32F2F),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        // 🗑️ BOTÃO DE EXCLUIR
                        IconButton(
                            onClick = {
                                itemParaExcluir = ui
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Excluir item",
                                tint = Color(0xFFD32F2F)
                            )
                        }
                    }
                }
            }
        }
    }

    // 🔥 DIÁLOGO DE CONFIRMAÇÃO
    itemParaExcluir?.let { ui ->

        AlertDialog(
            onDismissRequest = { itemParaExcluir = null },
            title = { Text("Excluir item") },
            text = {
                Text(
                    "Tem certeza que deseja excluir este item?\n\n${ui.item.nome}"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletarItem(ui.item)
                        itemParaExcluir = null
                    }
                ) {
                    Text("Excluir", color = Color(0xFFD32F2F))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { itemParaExcluir = null }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
