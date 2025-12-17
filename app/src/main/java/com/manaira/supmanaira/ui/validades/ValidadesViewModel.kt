package com.manaira.supmanaira.ui.validades

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manaira.supmanaira.data.local.entities.ItemEntity
import com.manaira.supmanaira.data.local.DatabaseProvider
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.combine

data class ItemValidadeUI(
    val item: ItemEntity,
    val diasParaVencer: Long,
    val nomeRegistro: String
)


class ValidadesViewModel(context: Context) : ViewModel() {

    private val db = DatabaseProvider.getDatabase(context)
    private val itemDao = db.itemDao()
    private val registroDao = db.registroDao()

    private val dateFormat =
        SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    val itensOrdenados: StateFlow<List<ItemValidadeUI>> =
        combine(
            itemDao.getTodosItensComValidade(),
            registroDao.getRegistros()
        ) { itens, registros ->

            val mapaRegistros = registros.associateBy { it.id }

            itens.mapNotNull { item ->
                val dias = diasParaVencer(item.validade) ?: return@mapNotNull null
                val nomeRegistro =
                    mapaRegistros[item.registroId]?.nome ?: "Registro desconhecido"

                ItemValidadeUI(
                    item = item,
                    diasParaVencer = dias,
                    nomeRegistro = nomeRegistro
                )
            }.sortedBy { it.diasParaVencer }
        }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    fun deletarItem(item: ItemEntity) {
        viewModelScope.launch {
            itemDao.deletar(item)
        }
    }

    private fun diasParaVencer(data: String?): Long? {
        if (data.isNullOrBlank()) return null

        return try {
            val hoje = Date()
            val validade = dateFormat.parse(data) ?: return null
            TimeUnit.MILLISECONDS.toDays(validade.time - hoje.time)
        } catch (e: Exception) {
            null
        }
    }
}

