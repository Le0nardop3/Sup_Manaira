package com.manaira.supmanaira.ui.itens

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manaira.supmanaira.data.local.DatabaseProvider
import com.manaira.supmanaira.data.local.entities.ItemEntity
import com.manaira.supmanaira.data.repository.ItemRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ItemViewModel(
    private val registroId: Int,
    context: Context
) : ViewModel() {

    private val db = DatabaseProvider.getDatabase(context)
    private val repository = ItemRepository(db.itemDao())

    val itens = repository.getItens(registroId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun criarItem(nome: String, quantidade: Int, tipo: String, validade: String?) {
        viewModelScope.launch {
            repository.inserir(
                ItemEntity(
                    registroId = registroId,
                    nome = nome,
                    quantidade = quantidade,
                    tipo = tipo,
                    validade = validade
                )
            )
        }
    }

    fun atualizarItem(item: ItemEntity) {
        viewModelScope.launch {
            repository.atualizar(item)
        }
    }

    fun deletarItem(item: ItemEntity) {
        viewModelScope.launch {
            repository.deletar(item)
        }
    }
}
