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

data class ItemValidadeUI(
    val item: ItemEntity,
    val diasParaVencer: Long
)

class ValidadesViewModel(context: Context) : ViewModel() {

    private val dao = DatabaseProvider.getDatabase(context).itemDao()

    private val dateFormat =
        SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))

    val itensOrdenados: StateFlow<List<ItemValidadeUI>> =
        dao.getTodosItensComValidade()
            .map { lista ->
                lista.mapNotNull { item ->
                    val dias = diasParaVencer(item.validade)
                        ?: return@mapNotNull null

                    ItemValidadeUI(
                        item = item,
                        diasParaVencer = dias
                    )
                }.sortedBy { it.diasParaVencer }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    // 🔴 FUNÇÃO NOVA — usada pelo botão de lixeira
    fun deletarItem(item: ItemEntity) {
        viewModelScope.launch {
            dao.deletar(item)
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
